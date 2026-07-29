package com.nexters.hytime.gitit.auth

import android.content.Context

/**
 * Android 환경에서 [GoogleAuthenticatorFactory]를 구현한다.
 *
 * Android [Context]와 백엔드 검증용 Web Client ID를 보유하고,
 * 호출 시마다 새로운 [AndroidGoogleAuthenticator]를 생성한다.
 *
 * @property context Android 컨텍스트
 * @property serverClientId 백엔드 검증에 사용할 Google OAuth Web Client ID
 */
class AndroidGoogleAuthenticatorFactory(
    private val context: Context,
    private val serverClientId: String,
) : GoogleAuthenticatorFactory {
    override fun create(): GoogleAuthenticator = AndroidGoogleAuthenticator(context, serverClientId)
}
