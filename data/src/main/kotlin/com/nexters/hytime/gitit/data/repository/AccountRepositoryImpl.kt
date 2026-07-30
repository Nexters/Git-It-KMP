package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.AccountResponse
import com.nexters.hytime.gitit.data.dto.SignInWithGoogleRequest
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.Account
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.NetworkMethod
import com.nexters.hytime.gitit.network.api.NetworkRequest
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

/**
 * [AccountRepository]의 구현체다.
 *
 * Google ID Token을 백엔드 `/auth/google` 엔드포인트로 전송하고, 응답을
 * [Account] 도메인 모델로 매핑한다. 모든 실패를 [Result.failure]로 감싸
 * 반환하므로 호출자는 try-catch 없이 [Result]로 처리할 수 있다.
 *
 * @property networkClient HTTP 통신을 수행하는 클라이언트
 * @property baseUrl 백엔드 API 기준 URL
 * @property json 직렬화/역직렬화에 사용할 Json 인스턴스
 */
class AccountRepositoryImpl(
    private val networkClient: NetworkClient,
    private val baseUrl: String,
    private val json: Json,
) : AccountRepository {
    /**
     * Google ID Token을 백엔드로 전송해 계정을 인증한다.
     *
     * @param idToken Google이 발급한 OIDC ID Token
     * @return 백엔드 검증 결과를 담은 [Result]. 성공 시 계정 정보, 실패 시 예외.
     */
    override suspend fun signInWithGoogle(idToken: String): Result<Account> =
        try {
            val request =
                NetworkRequest(
                    url = "$baseUrl$PATH_SIGN_IN_GOOGLE",
                    method = NetworkMethod.POST,
                    headers = mapOf("Content-Type" to "application/json", "Accept" to "application/json"),
                    body =
                        json.encodeToString(
                            SignInWithGoogleRequest.serializer(),
                            SignInWithGoogleRequest(idToken),
                        ),
                )
            val response = networkClient.execute(request)

            if (response.statusCode !in 200..299) {
                return Result.failure(
                    NetworkException(
                        message = "백엔드 로그인 실패: ${response.statusCode}",
                        cause =
                            IllegalStateException(
                                "응답 본문 ${response.body.length}자 (민감 정보 보호를 위해 생략)",
                            ),
                    ),
                )
            }

            Result.success(
                json
                    .decodeFromString(AccountResponse.serializer(), response.body)
                    .toDomain(),
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: Exception) {
            Result.failure(e)
        }

    private companion object {
        /** Google ID Token 로그인 엔드포인트 경로다. */
        private const val PATH_SIGN_IN_GOOGLE = "/auth/google"
    }
}
