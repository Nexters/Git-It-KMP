@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.model.EssayAnswerResult
import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [RegisterProjectUseCase]의 입력 정리와 저장소 호출 조건을 검증한다. */
class RegisterProjectUseCaseTest {
    /** URL 주변 공백을 제거하고 선택한 학습 깊이를 저장소에 전달하는지 검증한다. */
    @Test
    fun registerProject_유효한입력이면_정리한값을전달한다() {
        val repository = RecordingProjectRepository()

        val result =
            runBlocking {
                RegisterProjectUseCase(repository)(
                    githubRepoUrl = " https://github.com/Nexters/Git-it-Server ",
                    quizLevel = ProjectQuizLevel.L3,
                )
            }

        assertEquals("project-127", result.getOrThrow().projectId)
        assertEquals("https://github.com/Nexters/Git-it-Server", repository.githubRepoUrl)
        assertEquals(ProjectQuizLevel.L3, repository.quizLevel)
    }

    /** 빈 URL은 저장소를 호출하지 않고 실패로 반환하는지 검증한다. */
    @Test
    fun registerProject_URL이비어있으면_실패를반환한다() {
        val repository = RecordingProjectRepository()

        val result = runBlocking { RegisterProjectUseCase(repository)(" ", ProjectQuizLevel.L1) }

        assertTrue(result.isFailure)
        assertEquals(0, repository.callCount)
    }
}

/** 프로젝트 등록 요청을 기록하고 고정 성공 결과를 반환한다. */
private class RecordingProjectRepository : ProjectRepository {
    /** 전달받은 GitHub 저장소 URL이다. */
    var githubRepoUrl: String? = null

    /** 전달받은 문제 학습 깊이다. */
    var quizLevel: ProjectQuizLevel? = null

    /** 프로젝트 등록 호출 횟수다. */
    var callCount: Int = 0

    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> {
        callCount += 1
        this.githubRepoUrl = githubRepoUrl
        this.quizLevel = quizLevel
        return Result.success(ProjectRegistration("project-127", ProjectGenerationStatus.Ready))
    }

    override suspend fun getProjects(
        page: Int,
        size: Int,
    ): Result<ProjectPage> = error("호출되면 안 됩니다.")

    override suspend fun getProjectDetail(projectId: String): Result<ProjectDetail> = error("호출되면 안 됩니다.")

    override suspend fun deleteProject(projectId: String): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun getLearningSet(
        projectId: String,
        setId: String,
    ): Result<LearningSet> = error("호출되면 안 됩니다.")

    override suspend fun submitChoiceAnswer(
        projectId: String,
        questionId: String,
        selectedIndex: Int,
    ): Result<ChoiceAnswerResult> = error("호출되면 안 됩니다.")

    override suspend fun submitEssayAnswer(
        projectId: String,
        questionId: String,
        text: String,
    ): Result<EssayAnswerResult> = error("호출되면 안 됩니다.")

    override suspend fun bookmarkQuestion(
        projectId: String,
        questionId: String,
        bookmarked: Boolean,
    ): Result<Boolean> = error("호출되면 안 됩니다.")

    override suspend fun getBookmarkedQuestions(projectId: String?): Result<BookmarkedQuestions> = error("호출되면 안 됩니다.")
}
