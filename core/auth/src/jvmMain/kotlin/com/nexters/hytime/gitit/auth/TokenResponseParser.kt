package com.nexters.hytime.gitit.auth

import java.util.Base64

/**
 * Google OAuth 토큰 엔드포인트 응답(JSON)을 파싱해 [GoogleAuthResult]로 변환한다.
 *
 * `core:auth`는 직렬화 라이브러리에 의존하지 않고, 토큰 응답의 최소 필드만
 * 수동으로 추출한다. `id_token`은 JWT이므로 중간 페이로드를 디코딩해
 * 프로필 정보(displayName, email, photoUrl)를 보충한다.
 */
internal object TokenResponseParser {
    /**
     * 토큰 응답 JSON에서 ID Token과 프로필 정보를 추출한다.
     *
     * @param json Google 토큰 엔드포인트 응답 본문
     * @return ID Token과 프로필 정보를 담은 [GoogleAuthResult]
     * @throws GoogleAuthException `id_token` 필드가 없는 경우
     */
    fun parse(json: String): GoogleAuthResult {
        val idToken =
            extractString(json, "id_token")
                ?: throw GoogleAuthException(
                    GoogleAuthFailureReason.UNKNOWN,
                    IllegalStateException("토큰 응답에 id_token이 없습니다"),
                )

        val profile = decodeJwtPayload(idToken)
        return GoogleAuthResult(
            idToken = idToken,
            displayName = profile["name"],
            email = profile["email"],
            photoUrl = profile["picture"],
        )
    }

    /**
     * JWT의 중간 페이로드 부분을 Base64URL 디코딩하여 클레임을 추출한다.
     *
     * 서명 검증은 백엔드에서 수행하므로 여기서는 클레임 읽기만 한다.
     */
    private fun decodeJwtPayload(jwt: String): Map<String, String> {
        return try {
            val parts = jwt.split(".")
            if (parts.size < 2) return emptyMap()
            val payload = parts[1]
            val decoded = Base64.getUrlDecoder().decode(payload).toString(Charsets.UTF_8)
            parseJsonObject(decoded)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * 간단한 JSON 문자열에서 최상위 키-값 쌍을 추출한다.
     *
     * 직렬화 라이브러리 없이 토큰 응답/클레임의 평면 JSON만 처리하기 위한 최소 구현이다.
     */
    private fun parseJsonObject(json: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val regex = Regex(""""(\w+)"\s*:\s*"([^"]*)"""")
        regex.findAll(json).forEach { match ->
            result[match.groupValues[1]] = match.groupValues[2]
        }
        return result
    }

    private fun extractString(
        json: String,
        key: String,
    ): String? {
        val regex = Regex(""""$key"\s*:\s*"([^"]+)"""")
        return regex.find(json)?.groupValues?.get(1)
    }
}
