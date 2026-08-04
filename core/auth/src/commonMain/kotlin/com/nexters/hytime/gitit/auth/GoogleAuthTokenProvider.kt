package com.nexters.hytime.gitit.auth

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider

/**
 * [GoogleAuthenticator]를 도메인 [AuthTokenProvider] 포트에 적응시키는 어댑터다.
 */
class GoogleAuthTokenProvider(
    private val authenticator: GoogleAuthenticator,
) : AuthTokenProvider {
    override suspend fun obtainToken(): String = authenticator.signIn()
}
