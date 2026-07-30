package com.nexters.hytime.gitit.auth

/**
 * Google 로그인 인증 결과를 나타낸다.
 *
 * 플랫폼(Android Credential Manager / Desktop OAuth 웹 플로우)과 무관하게
 * 백엔드 서버 검증에 필요한 ID Token을 하나의 형태로 통일한다.
 *
 * @property idToken Google이 발급한 OIDC ID Token (JWT). 백엔드 검증에 사용한다.
 * @property displayName Google 계정의 표시 이름. 없으면 `null`이다.
 * @property email Google 계정의 이메일 주소. 사용자가 동의하지 않으면 `null`이다.
 * @property photoUrl 프로필 이미지 URL. 없으면 `null`이다.
 */
data class GoogleAuthResult(
    val idToken: String,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
)

/**
 * Google 로그인으로 획득한 인증 정보를 제공하는 플랫폼 추상화다.
 *
 * 구현체:
 * - Android: Credential Manager + Google Identity Services
 * - Desktop: OAuth 2.0 Authorization Code + PKCE 웹 플로우
 *
 * 두 플랫폼 모두 최종적으로 [GoogleAuthResult]를 반환하므로, 상위 계층은
 * 플랫폼 차이를 알 필요가 없다.
 */
interface GoogleAuthenticator {
    /**
     * Google 로그인 절차를 시작하고 인증 결과를 반환한다.
     *
     * 이 함수는 사용자가 브라우저/시스템 UI에서 계정을 선택하고 동의하는 동안
     * 일시 중단된다. 사용자가 로그인을 취소하거나 오류가 발생하면
     * [GoogleAuthException]을 던진다.
     *
     * @return 인증에 성공한 경우 ID Token과 프로필 정보
     * @throws GoogleAuthException 사용자 취소, 네트워크 오류, 설정 누락 등
     * @throws kotlin.coroutines.cancellation.CancellationException 코루틴이 취소된 경우
     */
    suspend fun signIn(): GoogleAuthResult
}

/**
 * Google 로그인 과정에서 발생하는 모든 오류의 상위 타입이다.
 *
 * @param cause 원본 예외. 없으면 `null`이다.
 */
class GoogleAuthException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
