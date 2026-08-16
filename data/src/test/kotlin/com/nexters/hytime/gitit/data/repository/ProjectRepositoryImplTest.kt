@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [ProjectRepositoryImpl]의 프로젝트 API 계약과 응답 매핑을 검증한다. */
class ProjectRepositoryImplTest {
    /** 서버 명세의 경로와 요청 필드를 사용하고 반환된 프로젝트를 도메인 모델로 변환하는지 검증한다. */
    @Test
    fun registerProject_successUsesServerContractAndMapsProject() {
        val networkClient = FakeNetworkClient(SUCCESS_RESPONSE)

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
                    ProjectRepositoryImpl(FakeNetworkClient(response))
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
                    ProjectRepositoryImpl(FakeNetworkClient(response))
                        .registerProject(REPOSITORY_URL, ProjectQuizLevel.L1)
                }

            assertTrue(result.isFailure, response)
        }
    }

    /** 목록 조회가 페이지 정보를 쿼리 파라미터로 보내고 응답을 매핑하는지 검증한다. */
    @Test
    fun getProjects_성공하면_페이지를쿼리로보내고매핑한다() {
        val networkClient = FakeNetworkClient(PROJECT_LIST_RESPONSE)

        val page = runBlocking { ProjectRepositoryImpl(networkClient).getProjects(page = 2, size = 5) }.getOrThrow()

        assertEquals("GET", networkClient.requestedMethod)
        assertEquals("/api/v1/projects", networkClient.requestedPath)
        assertEquals(mapOf("page" to "2", "size" to "5"), networkClient.requestedQueryParameters)
        assertEquals(true, networkClient.requestedAuthenticated)
        assertEquals(true, page.hasNext)
        assertEquals(listOf("p1"), page.items.map { it.projectId })
        assertEquals(listOf("react"), page.items.map { it.repositoryName })
        assertEquals(listOf("TypeScript", "JavaScript"), page.items.first().techStack)
        assertEquals("Set 1", page.items.first().currentSetLabel)
        assertEquals("q1", page.items.first().nextProblemId)
        assertEquals(40, page.items.first().overallProgressPercent)
    }

    /** 서버 기본값과 같은 페이지 값을 기본으로 사용하는지 검증한다. */
    @Test
    fun getProjects_인자를생략하면_서버기본값을보낸다() {
        val networkClient = FakeNetworkClient(PROJECT_LIST_RESPONSE)

        runBlocking { ProjectRepositoryImpl(networkClient).getProjects() }.getOrThrow()

        assertEquals(
            mapOf(
                "page" to ProjectRepository.DEFAULT_PAGE.toString(),
                "size" to ProjectRepository.DEFAULT_PAGE_SIZE.toString(),
            ),
            networkClient.requestedQueryParameters,
        )
    }

    /** 페이지 인자가 범위를 벗어나면 요청하지 않고 실패로 처리하는지 검증한다. */
    @Test
    fun getProjects_페이지인자가범위밖이면_요청하지않고실패한다() {
        val negativePage = FakeNetworkClient(PROJECT_LIST_RESPONSE)
        val zeroSize = FakeNetworkClient(PROJECT_LIST_RESPONSE)

        val negativePageResult = runBlocking { ProjectRepositoryImpl(negativePage).getProjects(page = -1) }
        val zeroSizeResult = runBlocking { ProjectRepositoryImpl(zeroSize).getProjects(size = 0) }

        assertTrue(negativePageResult.isFailure)
        assertTrue(zeroSizeResult.isFailure)
        assertEquals("", negativePage.requestedMethod)
        assertEquals("", zeroSize.requestedMethod)
    }

    private companion object {
        /** 테스트 요청에 사용하는 GitHub 저장소 URL이다. */
        const val REPOSITORY_URL = "https://github.com/Nexters/Git-it-Server"

        /** 준비 상태 프로젝트를 반환하는 정상 서버 응답이다. */
        const val SUCCESS_RESPONSE =
            """{"success":true,"data":{"projectId":"project-127","status":"READY"}}"""

        private const val PROJECT_LIST_RESPONSE =
            """{"success":true,"data":{"items":[{"projectId":"p1","repositoryName":"react",""" +
                """"repositoryImageUrl":"https://example.com/a.png","techStack":["TypeScript","JavaScript"],""" +
                """"currentSetLabel":"Set 1","currentSetTitle":"라우팅","nextProblemId":"q1","overallProgressPercent":40}],""" +
                """"hasNext":true}}"""
    }
}
