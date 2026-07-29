package com.nexters.hytime.gitit.domain.model

/**
 * 로그인한 사용자 계정을 나타내는 도메인 모델이다.
 *
 * 외부 인증(Google)이나 네트워크 DTO의 구조를 모르며, UI와 비즈니스 로직에서
 * 사용하는 순수한 계정 정보만 담는다.
 *
 * @property id 백엔드가 발급한 계정 식별자
 * @property displayName 화면에 표시할 사용자 이름
 * @property email 이메일 주소
 * @property photoUrl 프로필 이미지 URL. 없으면 `null`이다.
 */
data class Account(
    val id: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
)
