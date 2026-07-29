package com.nexters.hytime.gitit.auth

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider

/**
 * [GoogleAuthenticator]를 도메인 [AuthTokenProvider] 포트에 적응시키는 어댑터다.
 *
 * 도메인이 구체적 인증 수단(Google)을 모르도록, [GoogleAuthenticator]에서
 * 얻은 [GoogleAuthResult]의 ID Token만 추출해 반환한다.
 *
 * @property authenticator 플랫폼별 Google 로그인 구현체
 */
class GoogleAuthTokenProvider(
    private val authenticator: GoogleAuthenticator,
) : AuthTokenProvider {
    override suspend fun obtainToken(): String = authenticator.signIn().idToken
}
