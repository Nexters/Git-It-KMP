package com.nexters.hytime.gitit.domain.model

/**
 * 백엔드 로그인으로 발급된 세션을 나타낸다.
 *
 * @property accessToken 인증이 필요한 API 요청에 사용할 토큰
 * @property refreshToken 액세스 토큰 재발급에 사용할 토큰
 * @property needsCuration 추가 온보딩 정보 입력이 필요한지 여부
 */
data class LoginSession(
    val accessToken: String,
    val refreshToken: String,
    val needsCuration: Boolean,
)
