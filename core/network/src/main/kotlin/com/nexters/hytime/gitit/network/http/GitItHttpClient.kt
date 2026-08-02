package com.nexters.hytime.gitit.network.http

import com.nexters.hytime.gitit.network.logging.NetworkLogger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.Logger as KtorLogger

/** 로그를 기록하지 않는 기본 네트워크 로거다. */
private val noOpNetworkLogger = NetworkLogger { }

/**
 * Git-It 서버와 통신할 Ktor [HttpClient]를 생성한다.
 *
 * 알 수 없는 응답 필드는 무시하며, HTTP 실패 응답은 데이터 계층에서 도메인 오류로 변환할 수 있도록 응답으로 반환한다.
 *
 * @param networkLogger Ktor HTTP 로그를 전달할 네트워크 로거
 * @return Git-It API 통신에 사용하는 HTTP 클라이언트
 */
internal fun createGitItHttpClient(networkLogger: NetworkLogger): HttpClient =
    HttpClient(OkHttp) {
        configureGitItHttpClient(networkLogger)
    }

/**
 * Git-It API 통신에 공통으로 적용할 [HttpClient] 설정을 구성한다.
 *
 * @receiver 설정을 적용할 HTTP 클라이언트 구성 객체
 * @param networkLogger Ktor HTTP 로그를 전달할 네트워크 로거
 */
internal fun HttpClientConfig<*>.configureGitItHttpClient(networkLogger: NetworkLogger = noOpNetworkLogger) {
    install(ContentNegotiation) {
        json(
            Json {
                explicitNulls = true
                ignoreUnknownKeys = true
                isLenient = true
            },
        )
    }

    install(Logging) {
        logger = createKtorLogger(networkLogger)
        level = LogLevel.ALL
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
}

private val sensitiveFieldRegex = Regex(""""(id_token|access_token|refresh_token)":"[^"]*"""")

private fun sanitizeSensitiveFields(message: String): String = message.replace(sensitiveFieldRegex, """"$1":"***"""")


/**
 * [networkLogger]를 Ktor HTTP 로거로 변환한다.
 *
 * @param networkLogger Ktor 로그를 전달할 네트워크 로거
 * @return Ktor Logging 플러그인에 전달할 로거
 */
private fun createKtorLogger(networkLogger: NetworkLogger): KtorLogger =
    object : KtorLogger {
        override fun log(message: String) {
            networkLogger.log(sanitizeSensitiveFields(message))
        }
    }
