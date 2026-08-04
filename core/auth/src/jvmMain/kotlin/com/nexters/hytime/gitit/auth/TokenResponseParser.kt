package com.nexters.hytime.gitit.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Google OAuth 토큰 엔드포인트 응답에서 `id_token`만 추출한다.
 */
internal object TokenResponseParser {
    @Serializable
    private data class TokenResponse(
        @SerialName("id_token") val idToken: String = "",
    )

    private val json = Json { ignoreUnknownKeys = true }

    /** 토큰 응답 JSON에서 ID Token을 추출한다. */
    fun parse(responseBody: String): String {
        val tokenResponse =
            try {
                json.decodeFromString<TokenResponse>(responseBody)
            } catch (e: Exception) {
                throw GoogleAuthException("토큰 응답 파싱 실패: ${e.message}", e)
            }
        if (tokenResponse.idToken.isBlank()) {
            throw GoogleAuthException("토큰 응답에 id_token이 없습니다")
        }
        return tokenResponse.idToken
    }
}
