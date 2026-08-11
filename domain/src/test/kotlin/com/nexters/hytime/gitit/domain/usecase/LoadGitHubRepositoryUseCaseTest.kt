package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.GitHubRepository
import com.nexters.hytime.gitit.domain.repository.GitHubRepositoryRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [LoadGitHubRepositoryUseCase]의 URL 검증과 조회 위임을 검증한다. */
class LoadGitHubRepositoryUseCaseTest {
    /** 정상 URL의 공백과 `.git` 접미사가 제거되는지 검증한다. */
    @Test
    fun invoke_validUrlNormalizesAndLoadsRepository() {
        var receivedOwner = ""
        var receivedName = ""
        val expected = GitHubRepository(name = "react", ownerName = "facebook", ownerAvatarUrl = "avatar")
        val useCase =
            LoadGitHubRepositoryUseCase(
                repository =
                    repository { owner, name ->
                        receivedOwner = owner
                        receivedName = name
                        Result.success(expected)
                    },
            )

        val result = runBlocking { useCase("  https://github.com/facebook/react.git/  ") }

        assertEquals("facebook", receivedOwner)
        assertEquals("react", receivedName)
        assertEquals(expected, result.getOrThrow())
    }

    /** 지원하지 않는 scheme, host, path가 실패 결과를 만드는지 검증한다. */
    @Test
    fun invoke_invalidSchemeHostOrPathReturnsFailure() {
        val useCase = LoadGitHubRepositoryUseCase(repository { _, _ -> error("호출되면 안 됩니다.") })
        val invalidUrls =
            listOf(
                "http://github.com/facebook/react",
                "https://example.com/facebook/react",
                "https://github.com/facebook",
                "https://github.com/facebook/react/issues",
                "https://github.com/facebook/react?tab=readme",
            )

        invalidUrls.forEach { url ->
            assertTrue(runBlocking { useCase(url) }.isFailure, url)
        }
    }

    private fun repository(block: suspend (String, String) -> Result<GitHubRepository>): GitHubRepositoryRepository =
        object : GitHubRepositoryRepository {
            override suspend fun getRepository(
                owner: String,
                name: String,
            ): Result<GitHubRepository> = block(owner, name)
        }
}
