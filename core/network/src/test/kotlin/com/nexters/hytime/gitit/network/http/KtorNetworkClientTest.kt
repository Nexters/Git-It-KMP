package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
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

@Serializable
private data class TestRequest(
    val value: String,
)

@Serializable
private data class TestResponse(
    val result: String,
)

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

    @Test
    fun post_non2xxThrowsNetworkException() {
        runBlocking {
            val c = client(status = HttpStatusCode.NotFound, body = """{"error":"no"}""")
            assertFailsWith<NetworkException> {
                c.post<TestRequest, TestResponse>("/test", TestRequest("x"))
            }
        }
    }
}
