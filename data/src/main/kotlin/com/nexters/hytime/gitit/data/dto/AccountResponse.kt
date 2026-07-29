package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 백엔드 로그인 응답 본문이다.
 *
 * 백엔드 API 스펙이 확정되기 전 임시 형태다. 필드명이나 구조가 바뀌면
 * 이 DTO와 [toDomain] 매핑만 수정하면 된다.
 *
 * @property id 백엔드가 발급한 계정 식별자
 * @property name 표시 이름
 * @property email 이메일 주소
 * @property profileImageUrl 프로필 이미지 URL. 없으면 `null`이다.
 */
@Serializable
data class AccountResponse(
    val id: String,
    val name: String,
    val email: String,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
)
