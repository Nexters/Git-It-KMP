package com.nexters.hytime.gitit.auth

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.awt.Desktop
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import kotlin.math.absoluteValue

/**
 * OAuth 2.0 Authorization Code + PKCE 플로우로 [GoogleAuthenticator]를 구현한다.
 *
 * 데스크톱에는 시스템 수준의 Google 로그인 API가 없으므로 다음 절차를 직접 수행한다:
 * 1. PKCE code_verifier / code_challenge 생성
 * 2. 시스템 브라우저로 Google 동의 화면 열기
 * 3. `127.0.0.1` 루프백 HTTP 서버에서 authorization code 수신
 * 4. authorization code를 ID Token으로 교환
 *
 * Google "Desktop 앱" 유형 OAuth 클라이언트를 사용하므로 client secret이 필요 없다.
 *
 * @property clientId Google OAuth "Desktop 앱" 클라이언트 ID
 * (`xxxxx.apps.googleusercontent.com`)
 * @property redirectUriPort 루프백 콜백 서버가 바인딩할 포트. 기본값 0은 사용 가능한 포트를
 * 자동 할당한다.
 */
class DesktopGoogleAuthenticator(
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUriPort: Int = 0,
) : GoogleAuthenticator {
    /**
     * 시스템 브라우저로 Google 로그인을 수행하고 authorization code를 ID Token으로 교환한다.
     *
     * @return ID Token과 프로필 정보를 담은 인증 결과
     * @throws GoogleAuthException 사용자 취소, 콜백 시간 초과, 토큰 교환 실패, 설정 오류
     */
    override suspend fun signIn(): GoogleAuthResult =
        try {
            withContext(Dispatchers.IO) {
                val pkce = PkceUtil.generate()
                val redirectUri = startCallbackServer()
                val state = randomState()
                val authUrl = buildAuthorizationUrl(pkce.challenge, redirectUri.url, state)

                openBrowser(authUrl)
                val code =
                    withTimeout(CALLBACK_TIMEOUT_MS) {
                        redirectUri.waitForCode(state)
                    }

                exchangeCodeForIdToken(code, pkce.verifier, redirectUri.url)
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: GoogleAuthException) {
            throw e
        } catch (e: Exception) {
            throw GoogleAuthException(GoogleAuthFailureReason.UNKNOWN, e)
        }

    private fun buildAuthorizationUrl(
        codeChallenge: String,
        redirectUri: String,
        state: String,
    ): String {
        val params =
            linkedMapOf(
                "client_id" to clientId,
                "redirect_uri" to redirectUri,
                "response_type" to "code",
                "scope" to SCOPES,
                "code_challenge" to codeChallenge,
                "code_challenge_method" to "S256",
                "state" to state,
            )
        return "$AUTH_BASE_URL?${params.toQueryString()}"
    }

    private fun exchangeCodeForIdToken(
        code: String,
        codeVerifier: String,
        redirectUri: String,
    ): GoogleAuthResult {
        val form =
            linkedMapOf(
                "client_id" to clientId,
                "code" to code,
                "code_verifier" to codeVerifier,
                "grant_type" to "authorization_code",
                "redirect_uri" to redirectUri,
                "client_secret" to clientSecret,
            ).toQueryString()

        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build()

        val response =
            HttpClient
                .newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            throw GoogleAuthException(
                GoogleAuthFailureReason.UNKNOWN,
                IllegalStateException("토큰 교환 실패: ${response.statusCode()} ${response.body()}"),
            )
        }

        return TokenResponseParser.parse(response.body())
    }

    private fun openBrowser(url: String) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(url))
                return
            }
        } catch (e: Exception) {
            // 폴백으로 플랫폼 명령 시도
        }
        openBrowserFallback(url)
    }

    private fun openBrowserFallback(url: String) {
        val os = System.getProperty("os.name").lowercase()
        val command =
            when {
                os.contains("mac") -> arrayOf("open", url)
                os.contains("win") -> arrayOf("rundll32", "url.dll,FileProtocolHandler", url)
                else -> arrayOf("xdg-open", url)
            }
        Runtime.getRuntime().exec(command)
    }

    private fun startCallbackServer(): CallbackServer = CallbackServer.start(redirectUriPort)

    private fun randomState(): String = PkceUtil.randomString(16)

    private fun Map<String, String>.toQueryString(): String =
        entries.joinToString("&") { (k, v) ->
            "${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"
        }

    internal companion object {
        private const val AUTH_BASE_URL = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val TOKEN_URL = "https://oauth2.googleapis.com/token"
        private const val SCOPES = "openid email profile"
        private const val CALLBACK_TIMEOUT_MS = 5 * 60 * 1000L
    }
}
