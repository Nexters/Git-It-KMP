package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.ApiResponse
import com.nexters.hytime.gitit.data.dto.EmptyApiResponse
import com.nexters.hytime.gitit.data.dto.LoginResponse
import com.nexters.hytime.gitit.data.dto.SignInWithGoogleRequest
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.repository.AuthRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.get
import com.nexters.hytime.gitit.network.api.post

/**
 * 백엔드 Auth API를 호출하는 저장소 구현체다.
 *
 * @property networkClient HTTP 구현을 숨긴 네트워크 클라이언트
 */
class AuthRepositoryImpl(
    private val networkClient: NetworkClient,
) : AuthRepository {
    override suspend fun verifyAccessToken(): Result<Unit> =
        runCatchingResult {
            networkClient
                .get<EmptyApiResponse>(PATH_VERIFY_ACCESS_TOKEN)
                .requireSuccess("토큰 검증 응답이 올바르지 않습니다.")
        }

    override suspend fun signInWithGoogle(idToken: String): Result<LoginSession> =
        runCatchingResult {
            val data =
                networkClient
                    .post<SignInWithGoogleRequest, ApiResponse<LoginResponse>>(
                        PATH_SIGN_IN_GOOGLE,
                        SignInWithGoogleRequest(idToken),
                        authenticated = false,
                    ).requireData(INVALID_LOGIN_RESPONSE)
            // 토큰이 비어 있으면 이후 인증 요청이 전부 실패하므로 로그인 자체를 실패로 본다.
            if (data.accessToken.isBlank() || data.refreshToken.isBlank()) {
                throw NetworkException(INVALID_LOGIN_RESPONSE)
            }
            data.toDomain()
        }

    private companion object {
        private const val PATH_VERIFY_ACCESS_TOKEN = "/api/v1/auth/token"
        private const val PATH_SIGN_IN_GOOGLE = "/api/v1/auth/login/google"
        private const val INVALID_LOGIN_RESPONSE = "로그인 응답이 올바르지 않습니다."
    }
}
