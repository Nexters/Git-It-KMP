package com.nexters.hytime.gitit.auth

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CancellationException

/**
 * Credential Manager와 Google Identity Services를 사용해 [GoogleAuthenticator]를 구현한다.
 *
 * @property context Android 컨텍스트
 * @property serverClientId 백엔드 검증에 사용할 Google OAuth Web Client ID
 */
class AndroidGoogleAuthenticator(
    private val context: Context,
    private val serverClientId: String,
) : GoogleAuthenticator {
    override suspend fun signIn(): String {
        val credential = getCredential()
        return try {
            credential.toGoogleIdToken()?.idToken
                ?: throw GoogleAuthException("Google ID Token 자격 증명이 아닙니다")
        } catch (e: GoogleIdTokenParsingException) {
            throw GoogleAuthException("Google ID Token 파싱에 실패했습니다", e)
        }
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
        } catch (noCredential: NoCredentialException) {
            throw GoogleAuthException("사용 가능한 Google 계정이 없습니다", noCredential)
        } catch (e: GetCredentialException) {
            throw GoogleAuthException("Credential Manager 오류: ${e.message}", e)
        } catch (e: Exception) {
            throw GoogleAuthException("Google 로그인 중 오류가 발생했습니다", e)
        }

    private fun Credential.toGoogleIdToken(): GoogleIdTokenCredential? {
        if (this !is CustomCredential) return null
        if (type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return null
        return GoogleIdTokenCredential.createFrom(data)
    }
}
