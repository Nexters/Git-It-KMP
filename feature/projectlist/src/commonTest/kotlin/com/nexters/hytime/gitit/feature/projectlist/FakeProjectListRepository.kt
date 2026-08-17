package com.nexters.hytime.gitit.feature.projectlist

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
 * 목록 조회에 지정한 결과만 돌려주는 테스트용 프로젝트 리포지토리다.
 *
 * @property pageResult 목록 조회에 돌려줄 결과
 * @property deleteResult 삭제 요청에 돌려줄 결과
 */
internal class FakeProjectListRepository(
    private val pageResult: Result<ProjectPage>,
    private val deleteResult: Result<Unit> = Result.success(Unit),
) : ProjectRepository {
    /** 마지막으로 삭제 요청한 프로젝트 식별자다. */
    var deletedProjectId: String? = null

    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> = error("호출되면 안 됩니다.")

    override suspend fun getProjects(
        page: Int,
        size: Int,
    ): Result<ProjectPage> = pageResult

    override suspend fun getProjectDetail(projectId: String): Result<ProjectDetail> = error("호출되면 안 됩니다.")

    override suspend fun deleteProject(projectId: String): Result<Unit> {
        deletedProjectId = projectId
        return deleteResult
    }

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
