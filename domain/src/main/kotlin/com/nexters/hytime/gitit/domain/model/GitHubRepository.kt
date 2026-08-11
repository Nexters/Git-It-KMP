package com.nexters.hytime.gitit.domain.model

/**
 * GitHub에서 조회한 학습 대상 저장소 정보다.
 *
 * @property name 저장소 이름
 * @property ownerName 저장소 소유자 로그인 이름
 * @property ownerAvatarUrl 저장소 소유자 아바타 URL
 */
data class GitHubRepository(
    val name: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
)
