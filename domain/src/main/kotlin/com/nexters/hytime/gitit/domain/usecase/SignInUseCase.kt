package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.repository.AuthRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult

/**
 * Google ID 토큰을 얻어 백엔드 로그인 세션으로 교환한다.
 *
 * @property tokenProvider 플랫폼 Google 인증으로 ID 토큰을 제공한다
 * @property authRepository ID 토큰을 백엔드 로그인 세션으로 교환한다
 * @property sessionStorage 발급받은 세션을 플랫폼 저장소에 보관한다
 */
class SignInUseCase(
    private val tokenProvider: AuthTokenProvider,
    private val authRepository: AuthRepository,
    private val sessionStorage: LoginSessionStorage,
) {
    /**
     * Google 로그인 전체 흐름을 실행한다.
     *
     * @return 저장을 마친 로그인 세션. 호출자는 [LoginSession.needsCuration]으로 다음 화면을 결정한다
     */
    suspend operator fun invoke(): Result<LoginSession> =
        runCatchingResult {
            val idToken = tokenProvider.obtainToken()
            authRepository.signInWithGoogle(idToken).getOrThrow().also { session ->
                sessionStorage.save(session)
            }
        }
}
