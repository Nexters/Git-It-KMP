package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
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
 */
internal class KtorNetworkClient(
    private val client: HttpClient,
    private val json: Json,
    private val baseUrl: String,
) : NetworkClient {
    override suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res =
        try {
            val response =
                client.post("$baseUrl$path") {
                    contentType(ContentType.Application.Json)
                    setBody(json.encodeToString(requestSerializer, body))
                }
            if (!response.status.isSuccess()) {
                throw NetworkException("요청 실패: ${response.status.value}")
            }
            json.decodeFromString(responseSerializer, response.body())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            throw NetworkException("네트워크 요청에 실패했습니다.", e)
        }
}
