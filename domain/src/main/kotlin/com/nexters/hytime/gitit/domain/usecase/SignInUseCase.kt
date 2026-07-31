package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.model.Account
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult

class SignInUseCase(
    private val tokenProvider: AuthTokenProvider,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(): Result<Account> =
        runCatchingResult {
            val idToken = tokenProvider.obtainToken()
            accountRepository.signInWithGoogle(idToken).getOrThrow()
        }
}
