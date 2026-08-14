package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.network.api.NetworkClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/** [GitHubRepositoryRepositoryImpl]의 요청과 응답 매핑을 검증한다. */
class GitHubRepositoryRepositoryImplTest {
    /** GitHub API URL과 User-Agent를 전달하고 도메인 모델로 변환하는지 검증한다. */
    @Test
    fun getRepository_successMapsResponseAndSendsGitHubRequest() {
        val networkClient = FakeNetworkClient()

        val result = runBlocking { GitHubRepositoryRepositoryImpl(networkClient).getRepository("facebook", "react") }

        assertEquals("https://api.github.com/repos/facebook/react", networkClient.requestedUrl)
        assertEquals("Git-It-KMP", networkClient.requestedHeaders["User-Agent"])
        assertEquals("react", result.getOrThrow().name)
        assertEquals("facebook", result.getOrThrow().ownerName)
        assertEquals("https://example.com/avatar.png", result.getOrThrow().ownerAvatarUrl)
    }
}

/** 테스트 응답을 역직렬화하며 마지막 GET 요청을 기록한다. */
private class FakeNetworkClient : NetworkClient {
    /** 마지막으로 요청한 전체 URL이다. */
    var requestedUrl: String = ""

    /** 마지막으로 요청한 헤더다. */
    var requestedHeaders: Map<String, String> = emptyMap()

    override suspend fun <Res : Any> get(
        url: String,
        headers: Map<String, String>,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedUrl = url
        requestedHeaders = headers
        return Json.decodeFromString(
            responseSerializer,
            """{"name":"react","owner":{"login":"facebook","avatar_url":"https://example.com/avatar.png"}}""",
        )
    }

    override suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        authenticated: Boolean,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res = error("호출되면 안 됩니다.")
}
