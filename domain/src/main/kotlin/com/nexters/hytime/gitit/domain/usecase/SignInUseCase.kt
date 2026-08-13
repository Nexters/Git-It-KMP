package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult

/**
 * Google ID 토큰을 얻어 백엔드 로그인 세션으로 교환한다.
 *
 * @property tokenProvider 플랫폼 Google 인증으로 ID 토큰을 제공한다
 * @property accountRepository ID 토큰을 백엔드 로그인 세션으로 교환한다
 * @property sessionStorage 발급받은 세션을 플랫폼 저장소에 보관한다
 */
class SignInUseCase(
    private val tokenProvider: AuthTokenProvider,
    private val accountRepository: AccountRepository,
    private val sessionStorage: LoginSessionStorage,
) {
    /**
     * Google 로그인 전체 흐름을 실행한다.
     *
     * @return 성공 시 백엔드 로그인 세션, 실패 시 원인을 담은 결과
     */
    suspend operator fun invoke(): Result<LoginSession> =
        runCatchingResult {
            val idToken = tokenProvider.obtainToken()
            accountRepository.signInWithGoogle(idToken).getOrThrow().also(sessionStorage::save)
        }
}
