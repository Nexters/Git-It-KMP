package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.get
import com.nexters.hytime.gitit.network.api.getAbsolute
import com.nexters.hytime.gitit.network.api.post
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Serializable
private data class TestRequest(
    val value: String,
)

@Serializable
private data class TestResponse(
    val result: String,
)

@Serializable
private data class AuthTestRequest(
    val idToken: String,
    val id_token: String,
    val deviceId: String,
    val deviceToken: String,
)

/** [KtorNetworkClient]의 요청 구성과 오류 변환을 검증한다. */
class KtorNetworkClientTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun client(
        status: HttpStatusCode = HttpStatusCode.OK,
        body: String = """{"result":"ok"}""",
        accessToken: String? = null,
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
        return KtorNetworkClient(
            client = httpClient,
            json = json,
            baseUrl = "https://example.com",
            accessTokenProvider = { accessToken },
        )
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

    /** 인증 요청에만 저장된 액세스 토큰을 Authorization 헤더로 전달하는지 검증한다. */
    @Test
    fun post_authenticationFlag_controlsAccessTokenHeader() {
        val authorizationHeaders = mutableListOf<String?>()
        val httpClient =
            HttpClient(
                MockEngine { request ->
                    authorizationHeaders += request.headers[HttpHeaders.Authorization]
                    respond(
                        content = """{"result":"ok"}""",
                        headers = headers { append("Content-Type", "application/json") },
                    )
                },
            ) {
                configureGitItHttpClient(NetworkLogger { })
            }
        val client =
            KtorNetworkClient(
                client = httpClient,
                json = json,
                baseUrl = "https://example.com",
                accessTokenProvider = { "access-token" },
            )

        runBlocking {
            client.post<TestRequest, TestResponse>("/test", TestRequest("authenticated"))
            client.post<TestRequest, TestResponse>("/login", TestRequest("login"), authenticated = false)
        }

        assertEquals(listOf("Bearer access-token", null), authorizationHeaders)
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
        var requestedAuthorization: String? = null
        val httpClient =
            HttpClient(
                MockEngine { request ->
                    requestedUrl = request.url.toString()
                    requestedUserAgent = request.headers["User-Agent"].orEmpty()
                    requestedAuthorization = request.headers[HttpHeaders.Authorization]
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
                KtorNetworkClient(
                    client = httpClient,
                    json = json,
                    baseUrl = "https://example.com",
                    accessTokenProvider = { "backend-access-token" },
                ).getAbsolute<TestResponse>("https://api.github.com/repos/facebook/react", mapOf("User-Agent" to "Git-It-KMP"))

            assertTrue(requestedUrl.startsWith("https://api.github.com/repos/facebook/react"))
            assertEquals("Git-It-KMP", requestedUserAgent)
            assertEquals(null, requestedAuthorization)
            assertEquals("ok", response.result)
        }
    }

    /** 경로 GET이 baseUrl을 앞에 붙이고 인증 여부에 따라 액세스 토큰을 전달하는지 검증한다. */
    @Test
    fun get_pathRequestPrependsBaseUrlAndControlsAccessTokenHeader() {
        val requestedUrls = mutableListOf<String>()
        val authorizationHeaders = mutableListOf<String?>()
        val httpClient =
            HttpClient(
                MockEngine { request ->
                    requestedUrls += request.url.toString()
                    authorizationHeaders += request.headers[HttpHeaders.Authorization]
                    respond(
                        content = """{"result":"ok"}""",
                        headers = headers { append("Content-Type", "application/json") },
                    )
                },
            ) {
                configureGitItHttpClient(NetworkLogger { })
            }
        val client =
            KtorNetworkClient(
                client = httpClient,
                json = json,
                baseUrl = "https://example.com",
                accessTokenProvider = { "access-token" },
            )

        runBlocking {
            val response = client.get<TestResponse>("/api/v1/members/me")
            client.get<TestResponse>("/api/v1/public", authenticated = false)

            assertEquals("ok", response.result)
        }

        assertEquals(listOf("https://example.com/api/v1/members/me", "https://example.com/api/v1/public"), requestedUrls)
        assertEquals(listOf("Bearer access-token", null), authorizationHeaders)
    }

    /** GET 실패 상태를 네트워크 예외로 변환하는지 검증한다. */
    @Test
    fun get_non2xxThrowsNetworkException() {
        runBlocking {
            assertFailsWith<NetworkException> {
                client(status = HttpStatusCode.NotFound).getAbsolute<TestResponse>("https://example.com/missing")
            }
        }
    }

    /** 요청과 응답 본문의 인증 토큰을 네트워크 로그에서 마스킹하는지 검증한다. */
    @Test
    fun post_sensitiveIdentifiersAreMaskedFromLogs() {
        val logs = mutableListOf<String>()
        val httpClient =
            HttpClient(
                MockEngine {
                    respond(
                        content =
                            """{"result":"ok","accessToken":"access-secret","refreshToken":"refresh-secret","access_token":"legacy-access","refresh_token":"legacy-refresh"}""",
                        headers = headers { append("Content-Type", "application/json") },
                    )
                },
            ) {
                configureGitItHttpClient(NetworkLogger(logs::add))
            }

        runBlocking {
            KtorNetworkClient(
                client = httpClient,
                json = json,
                baseUrl = "https://example.com",
                accessTokenProvider = { null },
            ).post<AuthTestRequest, TestResponse>(
                "/test",
                AuthTestRequest("id-secret", "legacy-id", "device-id", "device-token"),
            )
        }

        val message = logs.joinToString()
        assertFalse("id-secret" in message)
        assertFalse("legacy-id" in message)
        assertFalse("access-secret" in message)
        assertFalse("refresh-secret" in message)
        assertFalse("legacy-access" in message)
        assertFalse("legacy-refresh" in message)
        assertFalse("device-id" in message)
        assertFalse("device-token" in message)
    }
}
