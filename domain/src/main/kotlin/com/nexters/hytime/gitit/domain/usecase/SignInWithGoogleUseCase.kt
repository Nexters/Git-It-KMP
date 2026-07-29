package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.auth.GoogleAuthException
import com.nexters.hytime.gitit.auth.GoogleAuthenticator
import com.nexters.hytime.gitit.domain.model.Account
import com.nexters.hytime.gitit.domain.repository.AccountRepository

/**
 * Google 로그인 전체 흐름을 조율하는 유스케이스다.
 *
 * 두 단계를 순차적으로 수행한다:
 * 1. [GoogleAuthenticator]로 Google ID Token 획득 (플랫폼별 구현)
 * 2. [AccountRepository]로 ID Token을 백엔드에 전송해 검증된 계정 획득
 *
 * @property authenticator 플랫폼별 Google 로그인 구현체
 * @property accountRepository 계정 리포지토리
 */
class SignInWithGoogleUseCase(
    private val authenticator: GoogleAuthenticator,
    private val accountRepository: AccountRepository,
) {
    /**
     * Google 로그인을 수행하고 인증된 계정을 반환한다.
     *
     * @return 백엔드 검증을 거친 인증된 계정 정보
     * @throws GoogleAuthException Google 로그인 단계 실패
     * @throws Exception 백엔드 통신 단계 실패
     */
    suspend operator fun invoke(): Account {
        val authResult = authenticator.signIn()
        return accountRepository.signInWithGoogle(authResult.idToken)
    }
}
