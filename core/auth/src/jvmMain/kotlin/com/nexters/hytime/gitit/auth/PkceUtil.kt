package com.nexters.hytime.gitit.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * PKCE (Proof Key for Code Exchange)에 사용할 code_verifier / code_challenge 쌍을 생성한다.
 *
 * @property verifier 인증 요청 시 보내지 않고, 토큰 교환 시 서버로 전송하는 난수값.
 * @property challenge 인증 요청 URL에 포함되는 값. SHA-256(verifier)의 Base64URL 인코딩이다.
 */
internal data class PkcePair(
    val verifier: String,
    val challenge: String,
)

/**
 * PKCE 값 생성 유틸리티.
 *
 * RFC 7636 §4.1에 따라 43~128자의 무작위 문자열을 code_verifier로 사용하고,
 * §4.2에 따라 S256 방식으로 code_challenge를 계산한다.
 */
internal object PkceUtil {
    private val random = SecureRandom()
    private val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('-', '.', '_', '~')

    /**
     * 새로운 PKCE 쌍을 생성한다.
     *
     * @return verifier와 S256 challenge로 구성된 [PkcePair]
     */
    fun generate(): PkcePair {
        val verifier = randomString(64)
        val challenge = sha256Base64Url(verifier)
        return PkcePair(verifier, challenge)
    }

    /**
     * 지정된 길이의 URL 안전 무작위 문자열을 생성한다.
     *
     * @param length 문자열 길이
     * @return [charset] 범위의 무작위 문자열
     */
    fun randomString(length: Int): String =
        (1..length)
            .map { charset[random.nextInt(charset.size)] }
            .toCharArray()
            .concatToString()

    private fun sha256Base64Url(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }
}
