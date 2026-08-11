package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.get
import com.nexters.hytime.gitit.network.api.post
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@Serializable
private data class TestRequest(
    val value: String,
)

@Serializable
private data class TestResponse(
    val result: String,
)

/** [KtorNetworkClient]의 요청 구성과 오류 변환을 검증한다. */
class KtorNetworkClientTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun client(
        status: HttpStatusCode = HttpStatusCode.OK,
        body: String = """{"result":"ok"}""",
    ): NetworkClient {
        val httpClient =
            HttpClient(
                MockEngine {
                    respond(
                        content = body,
                        status = status,
                        headers = headers { append("Content-Type", "application/json") },
                    )
                },
            ) {
                configureGitItHttpClient(NetworkLogger { })
            }
        return KtorNetworkClient(httpClient, json, baseUrl = "https://example.com")
    }

    /** POST 성공 응답을 지정한 타입으로 역직렬화하는지 검증한다. */
    @Test
    fun post_successReturnsTypedResponse() {
        runBlocking {
            val response =
                client().post<TestRequest, TestResponse>(
                    "/test",
                    TestRequest("hello"),
                )
            assertEquals("ok", response.result)
        }
    }

    /** POST 실패 상태를 네트워크 예외로 변환하는지 검증한다. */
    @Test
    fun post_non2xxThrowsNetworkException() {
        runBlocking {
            val c = client(status = HttpStatusCode.NotFound, body = """{"error":"no"}""")
            assertFailsWith<NetworkException> {
                c.post<TestRequest, TestResponse>("/test", TestRequest("x"))
            }
        }
    }

    /** GET 요청이 절대 URL과 헤더를 전달하고 응답을 역직렬화하는지 검증한다. */
    @Test
    fun get_successSendsUrlAndHeadersAndReturnsTypedResponse() {
        var requestedUrl = ""
        var requestedUserAgent = ""
        val httpClient =
            HttpClient(
                MockEngine { request ->
                    requestedUrl = request.url.toString()
                    requestedUserAgent = request.headers["User-Agent"].orEmpty()
                    respond(
                        content = """{"result":"ok"}""",
                        headers = headers { append("Content-Type", "application/json") },
                    )
                },
            ) {
                configureGitItHttpClient(NetworkLogger { })
            }

        runBlocking {
            val response =
                KtorNetworkClient(httpClient, json, baseUrl = "https://example.com")
                    .get<TestResponse>("https://api.github.com/repos/facebook/react", mapOf("User-Agent" to "Git-It-KMP"))

            assertTrue(requestedUrl.startsWith("https://api.github.com/repos/facebook/react"))
            assertEquals("Git-It-KMP", requestedUserAgent)
            assertEquals("ok", response.result)
        }
    }

    /** GET 실패 상태를 네트워크 예외로 변환하는지 검증한다. */
    @Test
    fun get_non2xxThrowsNetworkException() {
        runBlocking {
            assertFailsWith<NetworkException> {
                client(status = HttpStatusCode.NotFound).get<TestResponse>("https://example.com/missing")
            }
        }
    }
}
