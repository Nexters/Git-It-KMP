package com.nexters.hytime.gitit.auth

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CompletableDeferred
import java.net.InetSocketAddress
import java.net.URLDecoder

/**
 * OAuth 2.0 authorization code를 수신하기 위한 루프백 HTTP 서버다.
 *
 * `127.0.0.1:{port}` 에 바인딩되고, Google 리다이렉트 콜백을 단 한 번 수신한 뒤 종료한다.
 *
 * @property url 브라우저에 등록할 redirect_uri (`http://127.0.0.1:{port}`)
 * @property server 바인딩된 [HttpServer]
 */
internal class CallbackServer private constructor(
    val url: String,
    private val server: HttpServer,
) {
    /**
     * authorization code가 도착할 때까지 대기한다.
     *
     * 콜백의 `state`가 [expectedState]와 일치하는지 검증하여 CSRF를 방지한다.
     *
     * @param expectedState 인증 요청 시 전송한 state 값
     * @return 수신한 authorization code
     */
    suspend fun waitForCode(expectedState: String): String {
        val deferred = codeDeferred ?: error("콜백 핸들러가 초기화되지 않았습니다")
        val (code, state) = deferred.await()
        if (state != expectedState) {
            throw GoogleAuthException(
                GoogleAuthFailureReason.UNKNOWN,
                IllegalStateException("state 불일치: CSRF가 의심됩니다"),
            )
        }
        return code
    }

    /** 콜백 서버를 종료한다. */
    fun stop() {
        server.stop(0)
    }

    private var codeDeferred: CompletableDeferred<Pair<String, String?>>? = null

    internal companion object {
        /**
         * 지정한 포트에 콜백 서버를 시작한다.
         *
         * @param port 바인딩할 포트. 0이면 운영체제가 사용 가능한 포트를 할당한다.
         * @return 시작된 [CallbackServer]
         */
        fun start(port: Int = 0): CallbackServer {
            val server = HttpServer.create(InetSocketAddress("127.0.0.1", port), 0)
            val actualPort = server.address.port
            val url = "http://127.0.0.1:$actualPort"

            val holder = CallbackServer(url, server)
            val deferred = CompletableDeferred<Pair<String, String?>>()
            holder.codeDeferred = deferred

            server.createContext("/") { exchange -> holder.handle(exchange, deferred) }
            server.start()

            return holder
        }
    }

    private fun handle(
        exchange: HttpExchange,
        deferred: CompletableDeferred<Pair<String, String?>>,
    ) {
        val query = exchange.requestURI.query ?: ""
        val params = parseQuery(query)
        val code = params["code"]
        val state = params["state"]
        val error = params["error"]

        val (status, html) =
            when {
                error == "access_denied" -> {
                    deferred.completeExceptionally(
                        GoogleAuthException(GoogleAuthFailureReason.CANCELED),
                    )
                    200 to htmlPage("로그인 취소", "로그인이 취소되었습니다. 이 창을 닫아도 됩니다.")
                }
                error != null -> {
                    deferred.completeExceptionally(
                        GoogleAuthException(
                            GoogleAuthFailureReason.UNKNOWN,
                            IllegalStateException("OAuth 오류: $error"),
                        ),
                    )
                    400 to htmlPage("오류", "로그인 중 오류가 발생했습니다: $error")
                }
                code != null -> {
                    deferred.complete(Pair(code, state))
                    200 to htmlPage("로그인 성공", "로그인에 성공했습니다. 이 창을 닫아도 됩니다.")
                }
                else -> {
                    400 to htmlPage("오류", "잘못된 요청입니다.")
                }
            }

        val body = html.toByteArray(Charsets.UTF_8)
        exchange.sendResponseHeaders(status, body.size.toLong())
        exchange.responseBody.use { it.write(body) }
        exchange.close()

        // 응답 후 즉시 서버 종료
        stop()
    }

    private fun parseQuery(query: String): Map<String, String> =
        if (query.isBlank()) {
            emptyMap()
        } else {
            query
                .split("&")
                .associate {
                    val parts = it.split("=", limit = 2)
                    val key = URLDecoder.decode(parts[0], "UTF-8")
                    val value = URLDecoder.decode(parts.getOrNull(1) ?: "", "UTF-8")
                    key to value
                }
        }

    private fun htmlPage(
        title: String,
        message: String,
    ): String =
        "<html><body style='font-family:sans-serif;text-align:center;padding:40px'>" +
            "<h2>$title</h2><p>$message</p></body></html>"
}
