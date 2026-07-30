package com.nexters.hytime.gitit.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Base64

/**
 * Google OAuth 토큰 엔드포인트 응답(JSON)을 파싱해 [GoogleAuthResult]로 변환한다.
 *
 * `id_token`은 JWT이므로 중간 페이로드를 Base64URL 디코딩한 뒤 같은 Json으로
 * 클레임을 파싱해 프로필 정보(displayName, email, photoUrl)를 보충한다.
 */
internal object TokenResponseParser {
    /** Google 토큰 엔드포인트 응답에서 추출할 최소 필드. */
    @Serializable
    private data class TokenResponse(
        @SerialName("id_token") val idToken: String,
    )

    /** JWT 페이로드에서 추출할 프로필 클레임. */
    @Serializable
    private data class JwtClaims(
        val name: String? = null,
        val email: String? = null,
        val picture: String? = null,
    )

    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    /**
     * 토큰 응답 JSON에서 ID Token과 프로필 정보를 추출한다.
     *
     * @param responseBody Google 토큰 엔드포인트 응답 본문
     * @return ID Token과 프로필 정보를 담은 [GoogleAuthResult]
     * @throws GoogleAuthException `id_token` 필드가 없는 경우
     */
    fun parse(responseBody: String): GoogleAuthResult {
        val tokenResponse =
            try {
                json.decodeFromString<TokenResponse>(responseBody)
            } catch (e: Exception) {
                throw GoogleAuthException("토큰 응답 파싱 실패: ${e.message}", e)
            }
        if (tokenResponse.idToken.isBlank()) {
            throw GoogleAuthException("토큰 응답에 id_token이 없습니다")
        }

        val claims = decodeJwtClaims(tokenResponse.idToken)
        return GoogleAuthResult(
            idToken = tokenResponse.idToken,
            displayName = claims?.name,
            email = claims?.email,
            photoUrl = claims?.picture,
        )
    }

    /**
     * JWT의 중간 페이로드 부분을 Base64URL 디코딩하여 클레임을 추출한다.
     *
     * 서명 검증은 백엔드에서 수행하므로 여기서는 클레임 읽기만 한다.
     * 파싱에 실패하면 `null`을 반환해 프로필 없이 ID Token만 유지한다.
     */
    private fun decodeJwtClaims(jwt: String): JwtClaims? =
        try {
            val payload = jwt.split(".").getOrElse(1) { return null }
            val decoded = Base64.getUrlDecoder().decode(payload).toString(Charsets.UTF_8)
            json.decodeFromString<JwtClaims>(decoded)
        } catch (e: Exception) {
            null
        }
}
