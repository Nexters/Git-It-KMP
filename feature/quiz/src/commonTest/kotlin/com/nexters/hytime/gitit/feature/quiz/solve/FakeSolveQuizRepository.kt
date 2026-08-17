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
 * @property choiceAnswerResult 4지선다 제출에 돌려줄 결과
 * @property essayAnswerResult 서술형 제출에 돌려줄 결과
 */
internal class FakeSolveQuizRepository(
    private val detailResult: Result<ProjectDetail>,
    private val learningSetResult: Result<LearningSet>,
    private val choiceAnswerResult: Result<ChoiceAnswerResult> =
        Result.failure(IllegalStateException("설정되지 않은 호출입니다.")),
    private val essayAnswerResult: Result<EssayAnswerResult> =
        Result.failure(IllegalStateException("설정되지 않은 호출입니다.")),
) : ProjectRepository {
    /** 마지막으로 서술형 답을 제출한 문제 식별자다. */
    var submittedEssayQuestionId: String? = null

    /** 마지막으로 제출한 서술형 답안이다. */
    var submittedEssayText: String? = null

    /** 마지막으로 4지선다 답을 제출한 문제 식별자다. */
    var submittedChoiceQuestionId: String? = null

    /** 마지막으로 제출한 선택지 번호다. */
    var submittedChoiceIndex: Int? = null

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
    ): Result<ChoiceAnswerResult> {
        submittedChoiceQuestionId = questionId
        submittedChoiceIndex = selectedIndex
        return choiceAnswerResult
    }

    override suspend fun submitEssayAnswer(
        projectId: String,
        questionId: String,
        text: String,
    ): Result<EssayAnswerResult> {
        submittedEssayQuestionId = questionId
        submittedEssayText = text
        return essayAnswerResult
    }

    override suspend fun bookmarkQuestion(
        projectId: String,
        questionId: String,
        bookmarked: Boolean,
    ): Result<Boolean> = error("호출되면 안 됩니다.")

    override suspend fun getBookmarkedQuestions(projectId: String?): Result<BookmarkedQuestions> = error("호출되면 안 됩니다.")
}
