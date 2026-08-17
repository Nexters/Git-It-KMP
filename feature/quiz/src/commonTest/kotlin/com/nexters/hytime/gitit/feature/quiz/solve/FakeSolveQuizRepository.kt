package com.nexters.hytime.gitit.feature.quiz.solve

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
 * 문제 풀이 테스트에 필요한 조회만 지원하는 테스트용 프로젝트 리포지토리다.
 *
 * @property detailResult 상세 조회에 돌려줄 결과
 * @property learningSetResult 학습 세트 조회에 돌려줄 결과
 */
internal class FakeSolveQuizRepository(
    private val detailResult: Result<ProjectDetail>,
    private val learningSetResult: Result<LearningSet>,
) : ProjectRepository {
    /** 마지막으로 학습 세트를 조회한 세트 식별자다. */
    var requestedSetId: String? = null

    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> = error("호출되면 안 됩니다.")

    override suspend fun getProjects(
        page: Int,
        size: Int,
    ): Result<ProjectPage> = error("호출되면 안 됩니다.")

    override suspend fun getProjectDetail(projectId: String): Result<ProjectDetail> = detailResult

    override suspend fun deleteProject(projectId: String): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun getLearningSet(
        projectId: String,
        setId: String,
    ): Result<LearningSet> {
        requestedSetId = setId
        return learningSetResult
    }

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
