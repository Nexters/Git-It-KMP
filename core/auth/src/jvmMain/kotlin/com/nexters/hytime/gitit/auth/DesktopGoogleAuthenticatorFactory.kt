package com.nexters.hytime.gitit.auth

/**
 * 데스크톱 환경에서 [GoogleAuthenticatorFactory]를 구현한다.
 *
 * Google "Desktop 앱" 유형 OAuth 클라이언트 ID를 보유하고,
 * 호출 시마다 새로운 [DesktopGoogleAuthenticator]를 생성한다.
 *
 * @property clientId Google OAuth "Desktop 앱" 클라이언트 ID
 * @property clientSecret Google OAuth "Desktop 앱" 클라이언트 보안 비밀. 토큰 교환에 필요하다.
 */
class DesktopGoogleAuthenticatorFactory(
    private val clientId: String,
    private val clientSecret: String,
) : GoogleAuthenticatorFactory {
    override fun create(): GoogleAuthenticator =
        DesktopGoogleAuthenticator(
            clientId = clientId,
            clientSecret = clientSecret,
        )
}
