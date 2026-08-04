package com.nexters.hytime.gitit.auth

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * OAuth 2.0 Authorization Code + PKCE 플로우로 [GoogleAuthenticator]를 구현한다.
 *
* 데스크톱에는 시스템 수준의 Google 로그인 API가 없으므로 다음 절차를 직접 수행한다:
 * 1. PKCE code_verifier / code_challenge 생성
 * 2. 시스템 브라우저로 Google 동의 화면 열기
 * 3. `127.0.0.1` 루프백 HTTP 서버에서 authorization code 수신
 * 4. authorization code를 ID Token으로 교환
 *
 * Google "Desktop 앱" 타입은 public client이므로 PKCE만으로 토큰을 교환한다.
 * 클라이언트 보안 비밀은 사용하지 않는다.
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
    override suspend fun signIn(): String =
        try {
            withContext(Dispatchers.IO) {
                val pkce = PkceUtil.generate()
                val (redirectUri, callbackServer) = CallbackServer.start(redirectUriPort)
                try {
                    val state = randomState()
                    val authUrl = buildAuthorizationUrl(pkce.challenge, redirectUri, state)

                    openBrowser(authUrl)
                    val code =
                        try {
                            withTimeout(CALLBACK_TIMEOUT_MS.milliseconds) {
                                callbackServer.waitForCode(state)
                            }
                        } catch (e: TimeoutCancellationException) {
                            throw GoogleAuthException("로그인 콜백 대기 시간을 초과했습니다", e)
                        }

                    exchangeCodeForIdToken(code, pkce.verifier, redirectUri)
                } finally {
                    callbackServer.stop()
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: GoogleAuthException) {
            throw e
        } catch (e: Exception) {
            throw GoogleAuthException("Google 로그인 중 오류가 발생했습니다", e)
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
    ): String {
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
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build()

        val response =
            HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            throw GoogleAuthException("토큰 교환 실패: ${response.statusCode()} ${response.body()}")
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
