package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 백엔드 Google 로그인 엔드포인트에 전송하는 요청 본문이다.
 *
 * @property idToken Google이 발급한 OIDC ID Token (JWT).
 * 백엔드에서 Google 공개키로 검증한 뒤 사용자를 식별한다.
 */
@Serializable
data class SignInWithGoogleRequest(
    @SerialName("idToken")
    val idToken: String,
)
