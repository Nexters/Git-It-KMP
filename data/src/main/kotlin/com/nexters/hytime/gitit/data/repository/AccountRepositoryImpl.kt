package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.LoginApiResponse
import com.nexters.hytime.gitit.data.dto.SignInWithGoogleRequest
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.post

/**
 * 계정 인증 API를 호출하는 저장소 구현체다.
 *
 * @property networkClient HTTP 구현을 숨긴 네트워크 클라이언트
 */
class AccountRepositoryImpl(
    private val networkClient: NetworkClient,
) : AccountRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<LoginSession> =
        runCatchingResult {
            val response =
                networkClient.post<SignInWithGoogleRequest, LoginApiResponse>(
                    PATH_SIGN_IN_GOOGLE,
                    SignInWithGoogleRequest(idToken),
                    authenticated = false,
                )
            val data =
                response.data?.takeIf {
                    response.success && it.accessToken.isNotBlank() && it.refreshToken.isNotBlank()
                } ?: throw NetworkException(response.message ?: "로그인 응답이 올바르지 않습니다.")
            data.toDomain()
        }

    private companion object {
        private const val PATH_SIGN_IN_GOOGLE = "/api/v1/auth/login/google"
    }
}
