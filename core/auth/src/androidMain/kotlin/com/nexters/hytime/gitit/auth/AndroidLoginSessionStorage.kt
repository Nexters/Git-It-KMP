package com.nexters.hytime.gitit.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android Keystore로 암호화한 로그인 세션을 앱 전용 저장소에 보관한다.
 *
 * @param context 자동 백업에서 제외되는 앱 전용 디렉터리를 제공할 컨텍스트
 */
class AndroidLoginSessionStorage(
    context: Context,
) : LoginSessionStorage {
    /** 암호화 키를 보관하는 Android Keystore다. */
    private val keyStore by lazy {
        KeyStore.getInstance(KEY_STORE_PROVIDER).apply {
            load(null)
        }
    }

    /** 암호화된 세션을 원자적으로 읽고 쓸 파일이다. */
    private val sessionFile = AtomicFile(context.applicationContext.noBackupFilesDir.resolve(SESSION_FILE_NAME))

    override suspend fun save(session: LoginSession) {
        withContext(Dispatchers.IO) {
            val encryptedSession = encryptSession(session, getOrCreateKey())
            val output = sessionFile.startWrite()
            try {
                output.write(encryptedSession)
                sessionFile.finishWrite(output)
            } catch (exception: Exception) {
                sessionFile.failWrite(output)
                throw exception
            }
        }
    }

    override suspend fun load(): LoginSession? =
        withContext(Dispatchers.IO) {
            if (!sessionFile.baseFile.exists()) return@withContext null

            try {
                decryptSession(sessionFile.readFully(), getOrCreateKey())
            } catch (_: Exception) {
                sessionFile.delete()
                runCatching { keyStore.deleteEntry(KEY_ALIAS) }
                null
            }
        }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            sessionFile.delete()
        }
    }

    /**
     * 기존 암호화 키를 반환하거나 처음 저장할 키를 생성한다.
     *
     * @return AES-GCM 암복호화에 사용할 Keystore 키
     */
    private fun getOrCreateKey(): SecretKey =
        keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            ?: KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_PROVIDER)
                .apply {
                    init(
                        KeyGenParameterSpec
                            .Builder(
                                KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                            ).setKeySize(KEY_SIZE_BITS)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setRandomizedEncryptionRequired(true)
                            .build(),
                    )
                }.generateKey()

    private companion object {
        /** Android Keystore 공급자 이름이다. */
        private const val KEY_STORE_PROVIDER = "AndroidKeyStore"

        /** 로그인 세션 암호화 키 별칭이다. */
        private const val KEY_ALIAS = "gitit_login_session"

        /** AES 키 크기다. */
        private const val KEY_SIZE_BITS = 256

        /** 암호화된 로그인 세션 파일 이름이다. */
        private const val SESSION_FILE_NAME = "login_session"
    }
}

/**
 * 로그인 세션을 AES-GCM으로 암호화하고 IV와 암호문을 하나의 바이트 배열로 묶는다.
 *
 * @param session 암호화할 로그인 세션
 * @param key AES-GCM 암호화 키
 * @return IV 길이, IV, 암호문 순서로 인코딩된 바이트 배열
 */
internal fun encryptSession(
    session: LoginSession,
    key: SecretKey,
): ByteArray {
    val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val encrypted = cipher.doFinal(Json.encodeToString(StoredLoginSession.fromDomain(session)).encodeToByteArray())

    return ByteArrayOutputStream().use { bytes ->
        DataOutputStream(bytes).use { output ->
            output.writeInt(cipher.iv.size)
            output.write(cipher.iv)
            output.write(encrypted)
        }
        bytes.toByteArray()
    }
}

/**
 * 저장된 바이트 배열을 AES-GCM으로 복호화한다.
 *
 * @param encryptedSession IV와 암호문이 들어 있는 바이트 배열
 * @param key AES-GCM 복호화 키
 * @return 복원된 로그인 세션
 * @throws IllegalArgumentException 저장 형식이 올바르지 않은 경우
 */
internal fun decryptSession(
    encryptedSession: ByteArray,
    key: SecretKey,
): LoginSession {
    val (iv, encrypted) =
        DataInputStream(ByteArrayInputStream(encryptedSession)).use { input ->
            val ivSize = input.readInt()
            require(ivSize == GCM_IV_SIZE_BYTES) { "로그인 세션 IV 길이가 올바르지 않습니다." }
            val iv = ByteArray(ivSize).also(input::readFully)
            val encrypted = input.readBytes()
            require(encrypted.size >= GCM_TAG_SIZE_BYTES) { "로그인 세션 암호문이 올바르지 않습니다." }
            iv to encrypted
        }
    val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_SIZE_BITS, iv))
    val storedSession = Json.decodeFromString<StoredLoginSession>(cipher.doFinal(encrypted).decodeToString())
    return storedSession.toDomain()
}

/**
 * 암호화 전에 직렬화할 로그인 세션 형식이다.
 *
 * @property accessToken 인증 API 요청에 사용할 토큰
 * @property refreshToken 액세스 토큰 재발급에 사용할 토큰
 * @property needsCuration 추가 온보딩 정보 입력이 필요한지 여부
 */
@Serializable
private data class StoredLoginSession(
    val accessToken: String,
    val refreshToken: String,
    val needsCuration: Boolean,
) {
    /**
     * 저장 형식을 도메인 모델로 변환한다.
     *
     * @return 복원된 로그인 세션
     */
    fun toDomain(): LoginSession = LoginSession(accessToken, refreshToken, needsCuration)

    companion object {
        /**
         * 도메인 모델을 저장 형식으로 변환한다.
         *
         * @param session 저장할 로그인 세션
         * @return 직렬화 가능한 로그인 세션
         */
        fun fromDomain(session: LoginSession): StoredLoginSession =
            StoredLoginSession(session.accessToken, session.refreshToken, session.needsCuration)
    }
}

/** AES-GCM 변환 이름이다. */
private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"

/** GCM IV 크기다. */
private const val GCM_IV_SIZE_BYTES = 12

/** GCM 인증 태그 크기다. */
private const val GCM_TAG_SIZE_BITS = 128

/** GCM 인증 태그의 바이트 크기다. */
private const val GCM_TAG_SIZE_BYTES = GCM_TAG_SIZE_BITS / Byte.SIZE_BITS
