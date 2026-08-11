package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GitHub 저장소 조회 응답이다.
 *
 * @property name 저장소 이름
 * @property owner 저장소 소유자 정보
 */
@Serializable
data class GitHubRepositoryResponse(
    val name: String,
    val owner: GitHubOwnerResponse,
)

/**
 * GitHub 저장소 소유자 응답이다.
 *
 * @property login 소유자 로그인 이름
 * @property avatarUrl 소유자 아바타 URL
 */
@Serializable
data class GitHubOwnerResponse(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
)
