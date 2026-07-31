package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.AccountResponse
import com.nexters.hytime.gitit.data.dto.SignInWithGoogleRequest
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.Account
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.post

class AccountRepositoryImpl(
    private val networkClient: NetworkClient,
) : AccountRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<Account> =
        runCatchingResult {
            networkClient.post<SignInWithGoogleRequest, AccountResponse>(
                PATH_SIGN_IN_GOOGLE,
                SignInWithGoogleRequest(idToken),
            ).toDomain()
        }

    private companion object {
        private const val PATH_SIGN_IN_GOOGLE = "/auth/google"
    }
}
