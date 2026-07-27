package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkRequest
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Ktor 구현체가 네트워크 경계를 지키는지 검증한다. */
class KtorNetworkClientTest {
    /** Ktor 로그를 외부에서 주입한 네트워크 로거로 전달한다. */
    @Test
    fun execute_networkLoggerIsProvidedForwardsKtorLogs() =
        runBlocking {
            val logs = mutableListOf<String>()
            val httpClient =
                HttpClient(
                    MockEngine {
                        respond(content = "", status = HttpStatusCode.OK)
                    },
                ) {
                    configureGitItHttpClient(NetworkLogger(logs::add))
                }
            val networkClient: NetworkClient = KtorNetworkClient(httpClient)

            networkClient.execute(NetworkRequest(url = "https://git-it.example.com/repositories"))

            assertTrue(logs.isNotEmpty())
            httpClient.close()
        }

    /** Ktor 응답을 구현 독립적인 응답 모델로 변환한다. */
    @Test
    fun execute_successResponseReturnsNetworkResponse() =
        runBlocking {
            val httpClient =
                HttpClient(
                    MockEngine {
                        respond(content = "{\"repository\":\"Git-It\"}", status = HttpStatusCode.OK)
                    },
                ) {
                    configureGitItHttpClient()
                }
            val networkClient: NetworkClient = KtorNetworkClient(httpClient)

            val response = networkClient.execute(NetworkRequest(url = "https://git-it.example.com/repositories"))

            assertEquals(HttpStatusCode.OK.value, response.statusCode)
            assertEquals("{\"repository\":\"Git-It\"}", response.body)

            httpClient.close()
        }

    /** HTTP 실패 응답의 상태 코드와 본문을 data 계층이 해석할 수 있도록 반환한다. */
    @Test
    fun execute_notFoundResponseReturnsNetworkResponse() =
        runBlocking {
            val httpClient =
                HttpClient(
                    MockEngine {
                        respond(content = "{\"message\":\"Not Found\"}", status = HttpStatusCode.NotFound)
                    },
                ) {
                    configureGitItHttpClient()
                }
            val networkClient: NetworkClient = KtorNetworkClient(httpClient)

            val response = networkClient.execute(NetworkRequest(url = "https://git-it.example.com/repositories"))

            assertEquals(HttpStatusCode.NotFound.value, response.statusCode)
            assertEquals("{\"message\":\"Not Found\"}", response.body)

            httpClient.close()
        }
}
