package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 공통 API 응답
 *
 * @property success 요청 성공 여부
 * @property data 성공 시 반환되는 로그인 데이터
 * @property message 실패 원인을 설명하는 서버 메시지
 */
@Serializable
internal data class LoginApiResponse(
    val success: Boolean,
    val data: LoginResponse? = null,
    val message: String? = null,
)

/**
 * 백엔드가 Google 로그인 성공 시 반환하는 토큰 정보다.
 *
 * @property accessToken 인증 API에 사용할 토큰
 * @property refreshToken 액세스 토큰 재발급에 사용할 토큰
 * @property needsCuration 추가 온보딩 정보 입력이 필요한지 여부
 */
@Serializable
internal data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val needsCuration: Boolean,
)
