package com.nexters.hytime.gitit.auth

import com.nexters.hytime.gitit.domain.model.LoginSession
import javax.crypto.KeyGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/** Android 로그인 세션의 암호화와 변조 감지를 검증한다. */
class AndroidLoginSessionStorageTest {
    /** 세션을 암호화한 뒤 같은 키로 원본을 복원할 수 있는지 검증한다. */
    @Test
    fun encryptSession_validSession_restoresOriginal() {
        val key = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        val session = LoginSession("access-token", "refresh-token", true)

        assertEquals(session, decryptSession(encryptSession(session, key), key))
    }

    /** 인증 태그가 변조된 암호문을 세션으로 복원하지 않는지 검증한다. */
    @Test
    fun decryptSession_tamperedCiphertext_fails() {
        val key = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        val encrypted = encryptSession(LoginSession("access-token", "refresh-token", false), key)
        encrypted[encrypted.lastIndex] = (encrypted.last().toInt() xor 1).toByte()

        assertFails { decryptSession(encrypted, key) }
    }
}
