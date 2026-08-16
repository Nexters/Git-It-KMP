package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

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
