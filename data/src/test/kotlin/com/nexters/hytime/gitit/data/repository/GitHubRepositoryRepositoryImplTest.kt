package com.nexters.hytime.gitit.data.repository

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/** [GitHubRepositoryRepositoryImpl]의 요청과 응답 매핑을 검증한다. */
class GitHubRepositoryRepositoryImplTest {
    /** GitHub API URL과 User-Agent를 전달하고 도메인 모델로 변환하는지 검증한다. */
    @Test
    fun getRepository_successMapsResponseAndSendsGitHubRequest() {
        val networkClient = FakeNetworkClient(REPOSITORY_RESPONSE)

        val result = runBlocking { GitHubRepositoryRepositoryImpl(networkClient).getRepository("facebook", "react") }

        assertEquals("https://api.github.com/repos/facebook/react", networkClient.requestedUrl)
        assertEquals("Git-It-KMP", networkClient.requestedHeaders["User-Agent"])
        assertEquals("react", result.getOrThrow().name)
        assertEquals("facebook", result.getOrThrow().ownerName)
        assertEquals("https://example.com/avatar.png", result.getOrThrow().ownerAvatarUrl)
    }

    private companion object {
        private const val REPOSITORY_RESPONSE =
            """{"name":"react","owner":{"login":"facebook","avatar_url":"https://example.com/avatar.png"}}"""
    }
}
