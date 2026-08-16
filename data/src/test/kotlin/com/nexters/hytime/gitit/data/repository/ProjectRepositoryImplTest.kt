@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.network.api.NetworkClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [ProjectRepositoryImpl]의 프로젝트 등록 API 계약과 응답 매핑을 검증한다. */
class ProjectRepositoryImplTest {
    /** 서버 명세의 경로와 요청 필드를 사용하고 반환된 프로젝트를 도메인 모델로 변환하는지 검증한다. */
    @Test
    fun registerProject_successUsesServerContractAndMapsProject() {
        val networkClient = ProjectFakeNetworkClient(SUCCESS_RESPONSE)

        val project =
            runBlocking {
                ProjectRepositoryImpl(networkClient)
                    .registerProject(REPOSITORY_URL, ProjectQuizLevel.L2)
                    .getOrThrow()
            }

        assertEquals("/api/v1/projects", networkClient.requestedPath)
        assertEquals("""{"githubRepoUrl":"$REPOSITORY_URL","quizLevel":"L2"}""", networkClient.requestBody)
        assertTrue(networkClient.requestedAuthenticated)
        assertEquals("project-127", project.projectId)
        assertEquals(ProjectGenerationStatus.Ready, project.status)
    }

    /** 서버의 모든 성공 상태를 앱 생성 상태로 변환하는지 검증한다. */
    @Test
    fun registerProject_supportedStatuses_mapsDomainStatus() {
        val statuses =
            mapOf(
                "READY" to ProjectGenerationStatus.Ready,
                "ANCHORED" to ProjectGenerationStatus.Anchored,
                "FAILED" to ProjectGenerationStatus.Failed,
                "COMPLETED" to ProjectGenerationStatus.Completed,
            )

        statuses.forEach { (serverStatus, expectedStatus) ->
            val response = """{"success":true,"data":{"projectId":"project-127","status":"$serverStatus"}}"""
            val result =
                runBlocking {
                    ProjectRepositoryImpl(ProjectFakeNetworkClient(response))
                        .registerProject(REPOSITORY_URL, ProjectQuizLevel.L1)
                }

            assertEquals(expectedStatus, result.getOrThrow().status)
        }
    }

    /** 실패 응답이나 지원하지 않는 상태를 프로젝트 등록 실패로 변환하는지 검증한다. */
    @Test
    fun registerProject_invalidResponse_returnsFailure() {
        val invalidResponses =
            listOf(
                """{"success":false,"message":"등록 실패"}""",
                """{"success":true,"data":null}""",
                """{"success":true,"data":{"projectId":"","status":"READY"}}""",
                """{"success":true,"data":{"projectId":"project-127","status":"REJECTED"}}""",
            )

        invalidResponses.forEach { response ->
            val result =
                runBlocking {
                    ProjectRepositoryImpl(ProjectFakeNetworkClient(response))
                        .registerProject(REPOSITORY_URL, ProjectQuizLevel.L1)
                }

            assertTrue(result.isFailure, response)
        }
    }

    private companion object {
        /** 테스트 요청에 사용하는 GitHub 저장소 URL이다. */
        const val REPOSITORY_URL = "https://github.com/Nexters/Git-it-Server"

        /** 준비 상태 프로젝트를 반환하는 정상 서버 응답이다. */
        const val SUCCESS_RESPONSE =
            """{"success":true,"data":{"projectId":"project-127","status":"READY"}}"""
    }
}

/** 테스트 응답을 역직렬화하며 마지막 프로젝트 등록 요청을 기록한다. */
private class ProjectFakeNetworkClient(
    private val responseBody: String,
) : NetworkClient {
    /** 마지막으로 요청한 API 경로다. */
    var requestedPath: String = ""

    /** 마지막으로 직렬화한 요청 본문이다. */
    var requestBody: String = ""

    /** 마지막 요청에 액세스 토큰 인증이 설정됐는지 여부다. */
    var requestedAuthenticated: Boolean = false

    override suspend fun <Res : Any> get(
        path: String,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res = error("호출되면 안 됩니다.")

    override suspend fun <Res : Any> getAbsolute(
        url: String,
        headers: Map<String, String>,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res = error("호출되면 안 됩니다.")

    override suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        authenticated: Boolean,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedPath = path
        requestBody = Json.encodeToString(requestSerializer, body)
        requestedAuthenticated = authenticated
        return Json.decodeFromString(responseSerializer, responseBody)
    }
}
