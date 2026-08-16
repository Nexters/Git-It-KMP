@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.QuestionFormat
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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

    /** 상세 조회가 프로젝트 경로로 요청하고 세트까지 매핑하는지 검증한다. */
    @Test
    fun getProjectDetail_성공하면_프로젝트경로로조회하고세트를매핑한다() {
        val networkClient = FakeNetworkClient(PROJECT_DETAIL_RESPONSE)

        val detail = runBlocking { ProjectRepositoryImpl(networkClient).getProjectDetail("p1") }.getOrThrow()

        assertEquals("GET", networkClient.requestedMethod)
        assertEquals("/api/v1/projects/p1", networkClient.requestedPath)
        assertEquals("https://github.com/facebook/react", detail.repositoryUrl)
        assertEquals(1000, detail.starCount)
        assertEquals(60, detail.overallProgressPercent)
        assertEquals(listOf("s1"), detail.sets.map { it.setId })
        assertEquals(5, detail.sets.first().problemCount)
        assertEquals(3, detail.sets.first().completedCount)
    }

    /** 삭제가 DELETE 메서드로 프로젝트 경로를 호출하는지 검증한다. */
    @Test
    fun deleteProject_성공하면_DELETE로요청한다() {
        val networkClient = FakeNetworkClient()

        val result = runBlocking { ProjectRepositoryImpl(networkClient).deleteProject("p1") }

        assertEquals(Unit, result.getOrThrow())
        assertEquals("DELETE", networkClient.requestedMethod)
        assertEquals("/api/v1/projects/p1", networkClient.requestedPath)
        assertEquals(true, networkClient.requestedAuthenticated)
    }

    /** 삭제 실패 응답을 실패로 변환하는지 검증한다. */
    @Test
    fun deleteProject_응답이실패면_실패를반환한다() {
        val networkClient = FakeNetworkClient("""{"success":false,"code":"PROJECT-001","message":"프로젝트를 찾을 수 없습니다"}""")

        val result = runBlocking { ProjectRepositoryImpl(networkClient).deleteProject("p1") }

        assertTrue(result.isFailure)
    }

    /** 식별자가 비어 있으면 요청을 보내지 않고 실패로 처리하는지 검증한다. */
    @Test
    fun 프로젝트식별자가비어있으면_요청하지않고실패한다() {
        val detailClient = FakeNetworkClient(PROJECT_DETAIL_RESPONSE)
        val deleteClient = FakeNetworkClient()

        val detailResult = runBlocking { ProjectRepositoryImpl(detailClient).getProjectDetail(" ") }
        val deleteResult = runBlocking { ProjectRepositoryImpl(deleteClient).deleteProject("") }

        assertTrue(detailResult.isFailure)
        assertTrue(deleteResult.isFailure)
        assertEquals("", detailClient.requestedMethod)
        assertEquals("", deleteClient.requestedMethod)
    }

    /** 학습 세트 조회가 중첩 경로로 요청하고 문제와 제출한 답을 매핑하는지 검증한다. */
    @Test
    fun getLearningSet_성공하면_중첩경로로조회하고문제를매핑한다() {
        val networkClient = FakeNetworkClient(LEARNING_SET_RESPONSE)

        val learningSet = runBlocking { ProjectRepositoryImpl(networkClient).getLearningSet("p1", "s1") }.getOrThrow()

        assertEquals("/api/v1/projects/p1/sets/s1", networkClient.requestedPath)
        assertEquals(ProjectQuizLevel.L2, learningSet.level)
        assertEquals("라우팅 흐름 따라가기", learningSet.title)
        assertEquals(2, learningSet.questions.size)

        val choiceQuestion = learningSet.questions.first()
        assertEquals(QuestionFormat.MULTIPLE_CHOICE, choiceQuestion.format)
        assertEquals(listOf("첫째", "둘째"), choiceQuestion.choices)
        assertEquals("src/index.ts", choiceQuestion.sources.first().file)
        assertEquals(1, choiceQuestion.myAnswer?.selectedIndex)
        assertEquals(true, choiceQuestion.myAnswer?.correct)
        assertEquals("2026-08-16T10:00:00Z", choiceQuestion.myAnswer?.answeredAt)

        val essayQuestion = learningSet.questions.last()
        assertEquals(QuestionFormat.ESSAY, essayQuestion.format)
        assertTrue(essayQuestion.choices.isEmpty())
        assertNull(essayQuestion.myAnswer)
    }

    /** 모르는 난이도·형식이 오면 null로 떨어뜨리는지 검증한다. */
    @Test
    fun getLearningSet_모르는열거형이면_null로매핑한다() {
        val networkClient =
            FakeNetworkClient(
                """{"success":true,"data":{"setId":"s1","level":"L9","questions":[{"questionId":"q1","format":"AUDIO"}]}}""",
            )

        val learningSet = runBlocking { ProjectRepositoryImpl(networkClient).getLearningSet("p1", "s1") }.getOrThrow()

        assertNull(learningSet.level)
        assertNull(learningSet.questions.first().format)
    }

    /** 4지선다 제출이 전용 경로로 선택지 번호를 보내고 채점 결과를 매핑하는지 검증한다. */
    @Test
    fun submitChoiceAnswer_성공하면_선택지번호를보내고결과를매핑한다() {
        val networkClient =
            FakeNetworkClient(
                """{"success":true,"data":{"questionId":"q1","correct":false,"answerIndex":2,"explanation":"두 번째가 맞습니다"}}""",
            )

        val result = runBlocking { ProjectRepositoryImpl(networkClient).submitChoiceAnswer("p1", "q1", 1) }.getOrThrow()

        assertEquals("POST", networkClient.requestedMethod)
        assertEquals("/api/v1/projects/p1/questions/q1/answers/choice", networkClient.requestedPath)
        assertEquals("""{"selectedIndex":1}""", networkClient.requestBody)
        assertEquals(false, result.correct)
        assertEquals(2, result.answerIndex)
        assertEquals("두 번째가 맞습니다", result.explanation)
    }

    /** 서술형 제출이 전용 경로로 답안을 보내고 채점 기준을 매핑하는지 검증한다. */
    @Test
    fun submitEssayAnswer_성공하면_답안을보내고채점기준을매핑한다() {
        val networkClient = FakeNetworkClient(ESSAY_ANSWER_RESPONSE)

        val result =
            runBlocking { ProjectRepositoryImpl(networkClient).submitEssayAnswer("p1", "q2", "라우터가 한곳에 모여 있습니다") }
                .getOrThrow()

        assertEquals("/api/v1/projects/p1/questions/q2/answers/essay", networkClient.requestedPath)
        assertEquals("""{"text":"라우터가 한곳에 모여 있습니다"}""", networkClient.requestBody)
        assertEquals("해설입니다", result.explanation)
        assertEquals(listOf("파일명을 들었는가"), result.rubric.criteria.map { it.text })
        assertEquals(listOf(3), result.rubric.criteria.map { it.points })
        assertEquals(listOf("라우터"), result.rubric.keyPoints)
        assertEquals("만점 예시", result.rubric.fullMarkExample)
    }

    /** 서술형 답안이 비었거나 서버 상한을 넘으면 요청하지 않고 실패로 처리하는지 검증한다. */
    @Test
    fun submitEssayAnswer_답안이비었거나상한을넘으면_요청하지않고실패한다() {
        val blankClient = FakeNetworkClient(ESSAY_ANSWER_RESPONSE)
        val tooLongClient = FakeNetworkClient(ESSAY_ANSWER_RESPONSE)

        val blankResult = runBlocking { ProjectRepositoryImpl(blankClient).submitEssayAnswer("p1", "q2", " ") }
        val tooLongResult =
            runBlocking {
                ProjectRepositoryImpl(tooLongClient)
                    .submitEssayAnswer("p1", "q2", "가".repeat(ProjectRepository.MAX_ESSAY_TEXT_LENGTH + 1))
            }

        assertTrue(blankResult.isFailure)
        assertTrue(tooLongResult.isFailure)
        assertEquals("", blankClient.requestedMethod)
        assertEquals("", tooLongClient.requestedMethod)
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

        private const val PROJECT_DETAIL_RESPONSE =
            """{"success":true,"data":{"projectId":"p1","repositoryUrl":"https://github.com/facebook/react",""" +
                """"repositoryName":"react","repositoryImageUrl":"https://example.com/a.png","starCount":1000,""" +
                """"techStack":["TypeScript"],"overallProgressPercent":60,"nextProblemId":"q1",""" +
                """"sets":[{"setId":"s1","label":"Set 1","title":"라우팅","problemCount":5,"completedCount":3}]}}"""

        private const val LEARNING_SET_RESPONSE =
            """{"success":true,"data":{"setId":"s1","title":"라우팅 흐름 따라가기","description":"설명","orientation":"안내",""" +
                """"level":"L2","questions":[""" +
                """{"questionId":"q1","format":"MULTIPLE_CHOICE","text":"문제1","choices":["첫째","둘째"],""" +
                """"sources":[{"file":"src/index.ts","startLine":1,"endLine":40,"symbol":"Router","summary":null,""" +
                """"url":"https://github.com/facebook/react/blob/abc/src/index.ts"}],""" +
                """"myAnswer":{"selectedIndex":1,"text":null,"correct":true,"answeredAt":"2026-08-16T10:00:00Z"}},""" +
                """{"questionId":"q2","format":"ESSAY","text":"문제2","choices":[],"sources":[],"myAnswer":null}]}}"""

        private const val ESSAY_ANSWER_RESPONSE =
            """{"success":true,"data":{"questionId":"q2","explanation":"해설입니다","rubric":{""" +
                """"criteria":[{"text":"파일명을 들었는가","points":3}],"keyPoints":["라우터"],""" +
                """"fullMarkExample":"만점 예시","partialExample":"부분 예시","zeroExample":"0점 예시"}}}"""
    }
}
