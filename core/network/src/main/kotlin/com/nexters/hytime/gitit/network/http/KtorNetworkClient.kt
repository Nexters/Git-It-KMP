package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.NetworkMethod
import com.nexters.hytime.gitit.network.api.NetworkRequest
import com.nexters.hytime.gitit.network.api.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CancellationException

/** Ktor를 사용해 [NetworkClient] 계약을 구현한다. */
internal class KtorNetworkClient(
    private val client: HttpClient,
) : NetworkClient {
    /**
     * [request]를 Ktor 요청으로 변환해 전송한다.
     *
     * @param request 서버에 전송할 구현 독립적인 요청 정보
     * @return Ktor 타입을 포함하지 않는 응답 정보
     * @throws NetworkException 요청을 전송하거나 응답을 읽지 못한 경우
     */
    override suspend fun execute(request: NetworkRequest): NetworkResponse =
        try {
            client
                .request(request.url) {
                    method = request.method.toKtorHttpMethod()
                    request.headers.forEach { (name, value) -> headers.append(name, value) }
                    request.body?.let(::setBody)
                }.let { response ->
                    NetworkResponse(
                        statusCode = response.status.value,
                        headers = response.headers.names().associateWith { name -> response.headers.getAll(name).orEmpty() },
                        body = response.bodyAsText(),
                    )
                }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw NetworkException(message = "네트워크 요청에 실패했습니다.", cause = exception)
        }
}

/** [NetworkMethod]를 Ktor 내부 HTTP 메서드로 변환한다. */
private fun NetworkMethod.toKtorHttpMethod(): HttpMethod =
    when (this) {
        NetworkMethod.GET -> HttpMethod.Get
        NetworkMethod.POST -> HttpMethod.Post
        NetworkMethod.PUT -> HttpMethod.Put
        NetworkMethod.PATCH -> HttpMethod.Patch
        NetworkMethod.DELETE -> HttpMethod.Delete
    }
