package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Ktor의 ContentNegotiation으로 직렬화/역직렬화를 수행하는 [NetworkClient] 구현체다.
 *
 * @property client ContentNegotiation(json)이 설치된 Ktor HttpClient
 * @property json 직렬화에 사용할 Json 인스턴스
 * @property baseUrl 모든 요청의 기준 URL. [post]에 전달하는 path 앞에 붙는다.
 * @property accessTokenProvider 현재 로그인 세션의 액세스 토큰을 제공한다.
 */
internal class KtorNetworkClient(
    private val client: HttpClient,
    private val json: Json,
    private val baseUrl: String,
    private val accessTokenProvider: suspend () -> String?,
) : NetworkClient {
    /** 액세스 토큰을 전달할 백엔드 origin이다. */
    private val backendUrl = Url(baseUrl)

    override suspend fun <Res : Any> get(
        path: String,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res =
        getAbsolute(
            url = "$baseUrl$path",
            authenticated = authenticated,
            responseSerializer = responseSerializer,
        )

    override suspend fun <Res : Any> getAbsolute(
        url: String,
        headers: Map<String, String>,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res =
        request {
            val accessToken = accessToken(url, authenticated)
            val response =
                client.get(url) {
                    accessToken?.let { bearerAuth(it) }
                    headers.forEach { (name, value) -> this.headers.append(name, value) }
                }
            if (!response.status.isSuccess()) {
                throw NetworkException("요청 실패: ${response.status.value}")
            }
            json.decodeFromString(responseSerializer, response.body())
        }

    override suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        authenticated: Boolean,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res =
        request {
            val url = "$baseUrl$path"
            val accessToken = accessToken(url, authenticated)
            val response =
                client.post(url) {
                    accessToken?.let { bearerAuth(it) }
                    contentType(ContentType.Application.Json)
                    setBody(json.encodeToString(requestSerializer, body))
                }
            if (!response.status.isSuccess()) {
                throw NetworkException("요청 실패: ${response.status.value}")
            }
            json.decodeFromString(responseSerializer, response.body())
        }

    /**
     * 인증 대상 백엔드 요청에 사용할 액세스 토큰을 반환한다.
     *
     * @param url 요청 URL
     * @param authenticated 인증 헤더가 필요한 요청인지 여부
     * @return 비어 있지 않은 액세스 토큰. 인증 대상이 아니면 `null`
     */
    private suspend fun accessToken(
        url: String,
        authenticated: Boolean,
    ): String? {
        if (!authenticated || !isBackendUrl(url)) return null
        return accessTokenProvider()?.takeIf(String::isNotBlank)
    }

    /**
     * 요청 URL이 설정된 백엔드와 같은 origin인지 확인한다.
     *
     * @param url 확인할 요청 URL
     * @return scheme, host, port가 모두 같으면 `true`
     */
    private fun isBackendUrl(url: String): Boolean =
        Url(url).let { requestUrl ->
            requestUrl.protocol == backendUrl.protocol &&
                requestUrl.host.equals(backendUrl.host, ignoreCase = true) &&
                requestUrl.port == backendUrl.port
        }

    /**
     * 네트워크 예외를 공통 오류 타입으로 변환하되 코루틴 취소는 그대로 전파한다.
     *
     * @param block 실행할 HTTP 요청
     * @return 요청 결과
     */
    private suspend fun <T> request(block: suspend () -> T): T =
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            throw NetworkException("네트워크 요청에 실패했습니다.", e)
        }
}
