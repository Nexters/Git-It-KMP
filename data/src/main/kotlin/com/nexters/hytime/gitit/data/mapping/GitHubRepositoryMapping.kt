package com.nexters.hytime.gitit.data.mapping

import com.nexters.hytime.gitit.data.dto.GitHubRepositoryResponse
import com.nexters.hytime.gitit.domain.model.GitHubRepository

/**
 * GitHub 응답을 도메인 저장소 정보로 변환한다.
 *
 * @return UI와 UseCase가 사용할 저장소 정보
 */
internal fun GitHubRepositoryResponse.toDomain(): GitHubRepository =
    GitHubRepository(
        name = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
    )
