package com.nexters.hytime.gitit.auth

/**
 * Google 로그인으로 ID Token을 획득하는 플랫폼 추상화다.
 *
 * 구현체:
 * - Android: Credential Manager + Google Identity Services
 * - Desktop: OAuth 2.0 Authorization Code + PKCE 웹 플로우
 */
interface GoogleAuthenticator {
    /**
     * Google 로그인을 수행하고 ID Token을 반환한다.
     *
     * @return 백엔드 검증에 사용할 Google OIDC ID Token (JWT)
     * @throws GoogleAuthException 사용자 취소, 네트워크 오류, 설정 누락 등
     * @throws kotlin.coroutines.cancellation.CancellationException 코루틴이 취소된 경우
     */
    suspend fun signIn(): String
}

/**
 * Google 로그인 과정에서 발생하는 오류다.
 *
 * @param cause 원본 예외. 없으면 `null`이다.
 */
class GoogleAuthException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
