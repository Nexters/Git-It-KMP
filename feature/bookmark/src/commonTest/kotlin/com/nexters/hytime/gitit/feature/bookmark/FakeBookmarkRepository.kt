package com.nexters.hytime.gitit.feature.bookmark

import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.model.EssayAnswerResult
import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 북마크 목록 조회에 지정한 결과만 돌려주는 테스트용 프로젝트 리포지토리다.
 *
 * @property bookmarksResult 북마크 목록 조회에 돌려줄 결과
 */
internal class FakeBookmarkRepository(
    private val bookmarksResult: Result<BookmarkedQuestions>,
) : ProjectRepository {
    /** 북마크 설정 요청이 실패해야 하면 지정하는 결과다. null이면 요청 값을 그대로 적용한다. */
    var bookmarkResult: Result<Boolean>? = null

    /** 마지막으로 북마크를 설정한 문제 식별자다. */
    var bookmarkedQuestionId: String? = null

    /** 마지막으로 북마크를 설정한 프로젝트 식별자다. */
    var bookmarkedProjectId: String? = null

    /** 마지막으로 요청한 북마크 상태다. */
    var requestedBookmarked: Boolean? = null

    /** 마지막으로 목록 조회에 사용한 프로젝트 필터다. */
    var requestedProjectId: String? = null

    /** 목록 조회 호출 횟수다. */
    var loadCount: Int = 0

    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> = error("호출되면 안 됩니다.")

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
    ): Result<Boolean> {
        bookmarkedQuestionId = questionId
        bookmarkedProjectId = projectId
        requestedBookmarked = bookmarked
        return bookmarkResult ?: Result.success(bookmarked)
    }

    override suspend fun getBookmarkedQuestions(projectId: String?): Result<BookmarkedQuestions> {
        requestedProjectId = projectId
        loadCount += 1
        return bookmarksResult
    }
}
