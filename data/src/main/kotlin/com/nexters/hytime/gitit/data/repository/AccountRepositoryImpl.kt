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

/**
 * [AccountRepository]의 구현체다.
 *
 * Google ID Token을 백엔드 `/auth/google` 엔드포인트로 전송하고, 응답을
 * [Account] 도메인 모델로 매핑한다. Ktor 타입은 [NetworkClient] 뒤에 숨겨져
 * 있으므로 이 클래스는 HTTP 구현을 모른다.
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
     * @return 백엔드 검증을 거친 인증된 계정 정보
     * @throws NetworkException HTTP 통신 자체에 실패한 경우
     * @throws kotlinx.serialization.SerializationException 응답 본문 파싱에 실패한 경우
     */
    override suspend fun signInWithGoogle(idToken: String): Account {
        val request =
            NetworkRequest(
                url = "$baseUrl$PATH_SIGN_IN_GOOGLE",
                method = NetworkMethod.POST,
                headers = mapOf("Content-Type" to "application/json", "Accept" to "application/json"),
                body = json.encodeToString(SignInWithGoogleRequest.serializer(), SignInWithGoogleRequest(idToken)),
            )
        val response = networkClient.execute(request)

        if (response.statusCode !in 200..299) {
            throw NetworkException(
                message = "백엔드 로그인 실패: ${response.statusCode}",
                cause = IllegalStateException(response.body),
            )
        }

        return json
            .decodeFromString(AccountResponse.serializer(), response.body)
            .toDomain()
    }

    private companion object {
        private const val PATH_SIGN_IN_GOOGLE = "/auth/google"
    }
}
