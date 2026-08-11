package com.nexters.hytime.gitit.domain.repository

import com.nexters.hytime.gitit.domain.model.GitHubRepository

/** GitHub 저장소 메타데이터 조회 계약이다. */
interface GitHubRepositoryRepository {
    /**
     * 소유자와 저장소 이름으로 공개 저장소 정보를 조회한다.
     *
     * @param owner 저장소 소유자 로그인 이름
     * @param name 저장소 이름
     * @return 조회 결과. 저장소가 없거나 통신에 실패하면 실패 결과
     */
    suspend fun getRepository(
        owner: String,
        name: String,
    ): Result<GitHubRepository>
}
