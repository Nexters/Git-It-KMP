package com.nexters.hytime.gitit.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android Keystore로 토큰을 암호화해 앱 전용 환경설정에 보관한다.
 *
 * @property context 앱 전용 저장소와 Keystore에 접근할 컨텍스트
 */
class AndroidLoginSessionStorage(
    context: Context,
) : LoginSessionStorage {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun save(session: LoginSession) {
        preferences
            .edit()
            .putString(KEY_ACCESS_TOKEN, encrypt(session.accessToken))
            .putString(KEY_REFRESH_TOKEN, encrypt(session.refreshToken))
            .putBoolean(KEY_NEEDS_CURATION, session.needsCuration)
            .apply()
    }

    override fun load(): LoginSession? =
        try {
            val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null)?.let(::decrypt) ?: return null
            val refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null)?.let(::decrypt) ?: return null
            LoginSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                needsCuration = preferences.getBoolean(KEY_NEEDS_CURATION, false),
            )
        } catch (_: Exception) {
            clear()
            null
        }

    override fun clear() {
        preferences.edit().clear().apply()
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv + encrypted, Base64.NO_WRAP)
    }

    private fun decrypt(value: String): String {
        val bytes = Base64.decode(value, Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, bytes.copyOfRange(0, IV_SIZE)))
        return cipher.doFinal(bytes.copyOfRange(IV_SIZE, bytes.size)).toString(Charsets.UTF_8)
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            .apply {
                init(
                    KeyGenParameterSpec
                        .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build(),
                )
            }.generateKey()
    }

    private companion object {
        private const val PREFERENCES_NAME = "gitit_login_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_NEEDS_CURATION = "needs_curation"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "gitit_login_session_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
    }
}
