package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.model.Account
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import kotlin.coroutines.cancellation.CancellationException

/**
 * 로그인 전체 흐름을 조율하는 유스케이스다.
 *
 * 두 단계를 순차적으로 수행한다:
 * 1. [AuthTokenProvider]로 ID Token 획득 (플랫폼별 구현)
 * 2. [AccountRepository]로 ID Token을 백엔드에 전송해 검증된 계정 획득
 *
 * domain은 구체적 인증 수단(Google 등)을 모르며, [AuthTokenProvider] 포트만 의존한다.
 *
 * @property tokenProvider 인증 토큰을 제공하는 포트
 * @property accountRepository 계정 리포지토리
 */
class SignInUseCase(
    private val tokenProvider: AuthTokenProvider,
    private val accountRepository: AccountRepository,
) {
    /**
     * 로그인을 수행하고 인증된 계정을 반환한다.
     *
     * @return 백엔드 검증 결과를 담은 [Result]. 성공 시 계정 정보, 실패 시 예외.
     */
    suspend operator fun invoke(): Result<Account> {
        val idToken =
            try {
                tokenProvider.obtainToken()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (e: Exception) {
                return Result.failure(e)
            }
        return accountRepository.signInWithGoogle(idToken)
    }
}
