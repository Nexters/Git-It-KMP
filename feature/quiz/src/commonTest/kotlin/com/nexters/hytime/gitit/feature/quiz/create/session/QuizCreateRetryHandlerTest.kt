@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.quiz.create.session

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.domain.usecase.RegisterProjectUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [QuizCreateRetryHandler]의 서버 재등록과 세션 교체를 검증한다. */
class QuizCreateRetryHandlerTest {
    /** 실패 세션의 기존 조건으로 API를 호출하고 새 프로젝트 ID의 생성 세션을 시작한다. */
    @Test
    fun retry_registrationSucceeds_startsNewProjectSession() =
        runTest {
            val repository = RetryProjectRepository()
            val store = QuizCreateStore(nowMillis = { 1_000L }, scope = backgroundScope)
            val request = QuizCreateRequest(REPOSITORY_URL, ProjectQuizLevel.L3)
            store.start("old-project", request)
            store.fail("old-project")
            val handler = QuizCreateRetryHandler(RegisterProjectUseCase(repository), store)

            val result = handler.retry()

            assertTrue(result.isSuccess)
            assertEquals(REPOSITORY_URL, repository.githubRepoUrl)
            assertEquals(ProjectQuizLevel.L3, repository.quizLevel)
            assertEquals("new-project", store.state.value.projectId)
            assertEquals(QuizCreateStatus.InProgress, store.state.value.status)
            store.cancel()
        }

    /** API 재등록이 실패하면 기존 실패 세션을 유지하고 사용자가 다시 시도할 수 있게 한다. */
    @Test
    fun retry_registrationFails_keepsRetryableFailureSession() =
        runTest {
            val repository = RetryProjectRepository(Result.failure(IllegalStateException("등록 실패")))
            val store = QuizCreateStore(nowMillis = { 1_000L }, scope = backgroundScope)
            val request = QuizCreateRequest(REPOSITORY_URL, ProjectQuizLevel.L1)
            store.start("old-project", request)
            store.fail("old-project")
            val handler = QuizCreateRetryHandler(RegisterProjectUseCase(repository), store)

            val result = handler.retry()

            assertTrue(result.isFailure)
            assertEquals("old-project", store.state.value.projectId)
            assertEquals(QuizCreateStatus.Failed, store.state.value.status)
            store.cancel()
        }

    private companion object {
        /** 테스트에서 서버 프로젝트로 다시 등록할 저장소 URL이다. */
        const val REPOSITORY_URL = "https://github.com/Nexters/Git-It-KMP"
    }
}

/**
 * 재시도 요청을 기록하고 지정된 프로젝트 등록 결과를 반환한다.
 *
 * @property result 재등록 API 대신 반환할 테스트 결과
 */
private class RetryProjectRepository(
    private val result: Result<ProjectRegistration> =
        Result.success(ProjectRegistration("new-project", ProjectGenerationStatus.Ready)),
) : ProjectRepository {
    /** 마지막 재시도에 전달된 GitHub 저장소 URL이다. */
    var githubRepoUrl: String? = null

    /** 마지막 재시도에 전달된 문제 학습 깊이다. */
    var quizLevel: ProjectQuizLevel? = null

    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> {
        this.githubRepoUrl = githubRepoUrl
        this.quizLevel = quizLevel
        return result
    }
}
