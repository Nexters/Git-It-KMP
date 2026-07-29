package com.nexters.hytime.gitit.auth

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException

/**
 * Credential Manager와 Google Identity Services를 사용해 [GoogleAuthenticator]를 구현한다.
 *
 * Android에서는 시스템 수준의 Credential Manager가 Google 계정 선택 UI를 제공하고,
 * [GoogleIdTokenCredential]을 통해 ID Token을 직접 반환한다.
 *
 * 보안상 권장되는 패턴대로 서버 측 OAuth 클라이언트(Web Client ID)의 ID Token을
 * 요청한다. Android OAuth Client는 APK 서명으로 연결되므로 별도의 비밀 키가 필요 없다.
 *
 * @property context Android 컨텍스트. Credential Manager API 호출에 필요하다.
 * @property serverClientId 백엔드 검증에 사용할 Google OAuth Web Client ID.
 * `xxxxx.apps.googleusercontent.com` 형식이다.
 */
class AndroidGoogleAuthenticator(
    private val context: Context,
    private val serverClientId: String,
) : GoogleAuthenticator {
    /**
     * Credential Manager를 통해 Google 로그인을 수행한다.
     *
     * @return ID Token과 프로필 정보를 담은 인증 결과
     * @throws GoogleAuthException 사용자 취소, 설정 오류, 기타 Credential Manager 오류
     */
    override suspend fun signIn(): GoogleAuthResult {
        val credential = getCredential()
        val googleCredential =
            credential.toGoogleIdToken()
                ?: throw GoogleAuthException(
                    GoogleAuthFailureReason.UNKNOWN,
                    IllegalStateException("Google ID Token 자격 증명이 아닙니다"),
                )
        return googleCredential.toAuthResult()
    }

    private suspend fun getCredential(): Credential =
        try {
            val googleIdOption =
                GetGoogleIdOption
                    .Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .build()
            val request =
                GetCredentialRequest
                    .Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
            CredentialManager
                .create(context)
                .getCredential(context = context, request = request)
                .credential
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (cancellation: GetCredentialCancellationException) {
            throw GoogleAuthException(GoogleAuthFailureReason.CANCELED, cancellation)
        } catch (noCredential: NoCredentialException) {
            throw GoogleAuthException(GoogleAuthFailureReason.UNKNOWN, noCredential)
        } catch (e: GetCredentialException) {
            throw GoogleAuthException(e.toFailureReason(), e)
        } catch (e: Exception) {
            // Play Services 내부 오류 등 GetCredentialException 계열이 아닌 예외를 잡는다.
            throw GoogleAuthException(GoogleAuthFailureReason.UNKNOWN, e)
        }

    /**
     * Credential Manager 결과를 Google ID Token 자격 증명으로 변환한다.
     *
     * @return Google ID Token 자격 증명. 타입이 맞지 않으면 `null`.
     */
    private fun Credential.toGoogleIdToken(): GoogleIdTokenCredential? {
        if (this !is CustomCredential) return null
        if (type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return null
        return GoogleIdTokenCredential.createFrom(data)
    }

    private fun GoogleIdTokenCredential.toAuthResult() =
        GoogleAuthResult(
            idToken = idToken,
            displayName = displayName,
            email = id,
            photoUrl = profilePictureUri?.toString(),
        )

    private fun GetCredentialException.toFailureReason(): GoogleAuthFailureReason =
        when (this) {
            is GetCredentialCancellationException -> GoogleAuthFailureReason.CANCELED
            else -> GoogleAuthFailureReason.UNKNOWN
        }
}
