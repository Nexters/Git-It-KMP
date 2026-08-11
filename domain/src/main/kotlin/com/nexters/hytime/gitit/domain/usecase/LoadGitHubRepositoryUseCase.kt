package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.GitHubRepository
import com.nexters.hytime.gitit.domain.repository.GitHubRepositoryRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult

/**
 * GitHub 저장소 링크를 검증하고 저장소 정보를 조회한다.
 *
 * @property repository 저장소 정보를 제공하는 도메인 계약
 */
class LoadGitHubRepositoryUseCase(
    private val repository: GitHubRepositoryRepository,
) {
    /**
     * GitHub HTTPS 저장소 링크를 조회한다.
     *
     * @param repositoryUrl `https://github.com/{owner}/{repository}` 형식의 URL
     * @return 검증 및 조회 결과
     */
    suspend operator fun invoke(repositoryUrl: String): Result<GitHubRepository> =
        runCatchingResult {
            val match =
                GITHUB_REPOSITORY_URL.matchEntire(repositoryUrl.trim())
                    ?: throw IllegalArgumentException("올바르지 않은 GitHub 저장소 링크입니다.")
            val owner = match.groupValues[1]
            val name = match.groupValues[2].removeSuffix(".git")
            require(name.isNotEmpty()) { "저장소 이름이 비어 있습니다." }
            repository.getRepository(owner = owner, name = name).getOrThrow()
        }

    private companion object {
        private val GITHUB_REPOSITORY_URL =
            Regex(
                pattern = "^https://github\\.com/([A-Za-z0-9](?:[A-Za-z0-9-]{0,38}))/([A-Za-z0-9._-]+)/?$",
                option = RegexOption.IGNORE_CASE,
            )
    }
}
