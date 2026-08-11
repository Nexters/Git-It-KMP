package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.GitHubRepositoryResponse
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.GitHubRepository
import com.nexters.hytime.gitit.domain.repository.GitHubRepositoryRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.get

/**
 * 공개 GitHub REST API로 저장소 정보를 조회한다.
 *
 * @property networkClient HTTP 구현 세부 사항을 숨기는 네트워크 클라이언트
 */
class GitHubRepositoryRepositoryImpl(
    private val networkClient: NetworkClient,
) : GitHubRepositoryRepository {
    override suspend fun getRepository(
        owner: String,
        name: String,
    ): Result<GitHubRepository> =
        runCatchingResult {
            networkClient
                .get<GitHubRepositoryResponse>(
                    url = "$GITHUB_API_BASE_URL/repos/$owner/$name",
                    headers = mapOf("User-Agent" to "Git-It-KMP"),
                ).toDomain()
        }

    private companion object {
        private const val GITHUB_API_BASE_URL = "https://api.github.com"
    }
}
