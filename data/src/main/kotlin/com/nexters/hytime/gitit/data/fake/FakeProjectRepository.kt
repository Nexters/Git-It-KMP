package com.nexters.hytime.gitit.data.fake

import com.nexters.hytime.gitit.domain.model.AvailableProject
import com.nexters.hytime.gitit.domain.model.BookmarkedQuestion
import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.model.EssayAnswerResult
import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.LearningSetSummary
import com.nexters.hytime.gitit.domain.model.MyAnswer
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.model.ProjectSummary
import com.nexters.hytime.gitit.domain.model.Question
import com.nexters.hytime.gitit.domain.model.QuestionFormat
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/** 더미 답안의 제출 시각이다. 실제 시각이 의미를 갖지 않는 임시 데이터라 상수로 고정한다. */
private const val FAKE_ANSWERED_AT = "2026-01-01T09:00:00Z"

/**
 * 서버 없이 화면을 확인하기 위한 임시 더미 [ProjectRepository]다.
 *
 * 상태를 메모리에만 두므로 앱을 다시 켜면 초기 시드로 돌아간다. 문제를 풀거나 북마크를 누르면
 * 진행률·저장 목록이 실제로 따라 움직이도록 답안을 기록해, 화면 간 흐름까지 확인할 수 있다.
 *
 * 서버 연동이 끝나면 `fakeProjectModule`과 함께 통째로 지운다.
 */
class FakeProjectRepository : ProjectRepository {
    private val projects: MutableList<FakeProject> = fakeProjectSeed().toMutableList()
    private val answers: MutableMap<String, MyAnswer> = mutableMapOf()
    private val bookmarks: MutableSet<String> = mutableSetOf()
    private var registeredCount = 0

    init {
        seedProgress()
    }

    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> {
        registeredCount += 1
        val project = newFakeProject(githubRepoUrl, quizLevel, registeredCount)
        projects.add(0, project)
        return Result.success(
            ProjectRegistration(
                projectId = project.projectId,
                status = ProjectGenerationStatus.Completed,
            ),
        )
    }

    override suspend fun getProjects(
        page: Int,
        size: Int,
    ): Result<ProjectPage> {
        val from = page * size
        if (from >= projects.size) return Result.success(ProjectPage(items = emptyList(), hasNext = false))
        val until = minOf(from + size, projects.size)
        return Result.success(
            ProjectPage(
                items = projects.subList(from, until).map(::summaryOf),
                hasNext = until < projects.size,
            ),
        )
    }

    override suspend fun getProjectDetail(projectId: String): Result<ProjectDetail> {
        val project = projects.find { it.projectId == projectId } ?: return notFound("프로젝트", projectId)
        return Result.success(detailOf(project))
    }

    override suspend fun deleteProject(projectId: String): Result<Unit> {
        val removed = projects.removeAll { it.projectId == projectId }
        return if (removed) Result.success(Unit) else notFound("프로젝트", projectId)
    }

    override suspend fun getLearningSet(
        projectId: String,
        setId: String,
    ): Result<LearningSet> {
        val project = projects.find { it.projectId == projectId } ?: return notFound("프로젝트", projectId)
        val set = project.sets.find { it.setId == setId } ?: return notFound("학습 세트", setId)
        return Result.success(
            LearningSet(
                setId = set.setId,
                title = set.title,
                description = set.description,
                orientation = set.orientation,
                level = set.level,
                questions = set.questions.map { it.question.copy(myAnswer = answers[it.question.questionId]) },
            ),
        )
    }

    override suspend fun submitChoiceAnswer(
        projectId: String,
        questionId: String,
        selectedIndex: Int,
    ): Result<ChoiceAnswerResult> {
        val found = locate(projectId, questionId) ?: return notFound("문제", questionId)
        val correct = selectedIndex == found.question.answerIndex
        answers[questionId] =
            MyAnswer(
                selectedIndex = selectedIndex,
                text = null,
                correct = correct,
                answeredAt = FAKE_ANSWERED_AT,
            )
        return Result.success(
            ChoiceAnswerResult(
                questionId = questionId,
                correct = correct,
                answerIndex = found.question.answerIndex,
                explanation = found.question.explanation,
            ),
        )
    }

    override suspend fun submitEssayAnswer(
        projectId: String,
        questionId: String,
        text: String,
    ): Result<EssayAnswerResult> {
        val found = locate(projectId, questionId) ?: return notFound("문제", questionId)
        answers[questionId] =
            MyAnswer(
                selectedIndex = null,
                text = text,
                correct = null,
                answeredAt = FAKE_ANSWERED_AT,
            )
        return Result.success(
            EssayAnswerResult(
                questionId = questionId,
                explanation = found.question.explanation,
                rubric = fakeRubric(),
            ),
        )
    }

    override suspend fun bookmarkQuestion(
        projectId: String,
        questionId: String,
        bookmarked: Boolean,
    ): Result<Boolean> {
        locate(projectId, questionId) ?: return notFound("문제", questionId)
        if (bookmarked) bookmarks.add(questionId) else bookmarks.remove(questionId)
        return Result.success(bookmarked)
    }

    override suspend fun getBookmarkedQuestions(projectId: String?): Result<BookmarkedQuestions> {
        val all = bookmarks.mapNotNull(::locate)
        val filtered = all.filter { projectId == null || it.project.projectId == projectId }
        return Result.success(
            BookmarkedQuestions(
                totalCount = filtered.size,
                availableProjects =
                    all
                        .map { it.project }
                        .distinctBy { it.projectId }
                        .map { AvailableProject(projectId = it.projectId, projectName = it.repositoryName) },
                bookmarks =
                    filtered.map { found ->
                        BookmarkedQuestion(
                            projectId = found.project.projectId,
                            projectName = found.project.repositoryName,
                            setId = found.set.setId,
                            setLabel = found.set.label,
                            problemNumber = found.set.questions.indexOf(found.question) + 1,
                            questionId = found.question.question.questionId,
                            question = found.question.question.text,
                        )
                    },
            ),
        )
    }

    /**
     * 목록 화면이 쓰는 요약으로 변환한다. 진행률과 이어 풀 문제는 지금까지 기록된 답안에서 계산한다.
     *
     * @param project 변환할 더미 프로젝트
     * @return 진행률이 반영된 프로젝트 요약
     */
    private fun summaryOf(project: FakeProject): ProjectSummary {
        val next = nextUnsolved(project)
        val currentSet = next?.set ?: project.sets.last()
        return ProjectSummary(
            projectId = project.projectId,
            repositoryName = project.repositoryName,
            repositoryImageUrl = project.repositoryImageUrl,
            techStack = project.techStack,
            currentSetLabel = currentSet.label,
            currentSetTitle = currentSet.title,
            nextSetId = next?.set?.setId,
            nextQuestionId = next?.question?.question?.questionId,
            overallProgressPercent = progressOf(project),
        )
    }

    /**
     * 상세 화면이 쓰는 모델로 변환한다.
     *
     * @param project 변환할 더미 프로젝트
     * @return 세트별 완료 개수가 반영된 프로젝트 상세
     */
    private fun detailOf(project: FakeProject): ProjectDetail =
        ProjectDetail(
            projectId = project.projectId,
            repositoryUrl = project.repositoryUrl,
            repositoryName = project.repositoryName,
            repositoryImageUrl = project.repositoryImageUrl,
            starCount = project.starCount,
            techStack = project.techStack,
            overallProgressPercent = progressOf(project),
            nextProblemId = nextUnsolved(project)?.question?.question?.questionId,
            sets =
                project.sets.map { set ->
                    LearningSetSummary(
                        setId = set.setId,
                        label = set.label,
                        title = set.title,
                        problemCount = set.questions.size,
                        completedCount = solvedCount(set),
                    )
                },
        )

    /**
     * 프로젝트 전체 진행률을 계산한다.
     *
     * @param project 진행률을 구할 프로젝트
     * @return 푼 문제 비율(%). 문제가 없으면 0
     */
    private fun progressOf(project: FakeProject): Int {
        val total = project.sets.sumOf { it.questions.size }
        if (total == 0) return 0
        return project.sets.sumOf(::solvedCount) * 100 / total
    }

    /**
     * 세트에서 이미 답을 낸 문제 수를 센다.
     *
     * @param set 대상 학습 세트
     * @return 답안이 기록된 문제 개수
     */
    private fun solvedCount(set: FakeSet): Int = set.questions.count { answers.containsKey(it.question.questionId) }

    /**
     * 아직 답을 내지 않은 첫 문제를 찾는다.
     *
     * @param project 대상 프로젝트
     * @return 이어서 풀 문제 위치. 전부 풀었으면 `null`
     */
    private fun nextUnsolved(project: FakeProject): FakeQuestionLocation? =
        project.sets.firstNotNullOfOrNull { set ->
            set.questions
                .firstOrNull { !answers.containsKey(it.question.questionId) }
                ?.let { FakeQuestionLocation(project = project, set = set, question = it) }
        }

    /**
     * 문제 식별자로 소속 프로젝트와 세트를 찾는다.
     *
     * @param questionId 찾을 문제 식별자
     * @return 문제 위치. 없으면 `null`
     */
    private fun locate(questionId: String): FakeQuestionLocation? =
        projects.firstNotNullOfOrNull { project ->
            project.sets.firstNotNullOfOrNull { set ->
                set.questions
                    .firstOrNull { it.question.questionId == questionId }
                    ?.let { FakeQuestionLocation(project = project, set = set, question = it) }
            }
        }

    /**
     * 프로젝트 범위를 좁혀 문제를 찾는다.
     *
     * @param projectId 문제가 속해야 할 프로젝트 식별자
     * @param questionId 찾을 문제 식별자
     * @return 문제 위치. 프로젝트가 다르거나 없으면 `null`
     */
    private fun locate(
        projectId: String,
        questionId: String,
    ): FakeQuestionLocation? = locate(questionId)?.takeIf { it.project.projectId == projectId }

    /** 목록 화면에 진행률이 서로 다른 카드가 보이도록 일부 문제를 미리 푼 상태로 만든다. */
    private fun seedProgress() {
        val presolved =
            projects
                .flatMap { project -> project.sets.flatMap { set -> set.questions.map { project to it } } }
                .groupBy({ it.first }, { it.second })
                .flatMap { (project, questions) -> questions.take(project.presolvedCount) }
        presolved.forEach { solved ->
            answers[solved.question.questionId] =
                if (solved.question.format == QuestionFormat.ESSAY) {
                    MyAnswer(selectedIndex = null, text = "더미로 미리 제출해 둔 답안입니다.", correct = null, answeredAt = FAKE_ANSWERED_AT)
                } else {
                    MyAnswer(selectedIndex = solved.answerIndex, text = null, correct = true, answeredAt = FAKE_ANSWERED_AT)
                }
        }
        projects
            .firstOrNull()
            ?.sets
            ?.firstOrNull()
            ?.questions
            ?.firstOrNull()
            ?.let { bookmarks.add(it.question.questionId) }
        projects
            .getOrNull(1)
            ?.sets
            ?.firstOrNull()
            ?.questions
            ?.firstOrNull()
            ?.let { bookmarks.add(it.question.questionId) }
    }

    /**
     * 조회 실패를 도메인 [Result]로 감싼다.
     *
     * @param label 사람이 읽을 대상 이름
     * @param id 찾지 못한 식별자
     * @return 실패 결과
     */
    private fun <T> notFound(
        label: String,
        id: String,
    ): Result<T> = Result.failure(NoSuchElementException("더미 데이터에 없는 " + label + "입니다: " + id))
}

/**
 * 메모리에만 존재하는 더미 프로젝트다.
 *
 * @property projectId 프로젝트 식별자
 * @property repositoryName 저장소 이름
 * @property repositoryUrl GitHub 저장소 링크
 * @property repositoryImageUrl 저장소 소유자 프로필 이미지 URL
 * @property starCount GitHub 스타 수
 * @property techStack 기술 스택
 * @property presolvedCount 앱을 켰을 때 이미 푼 상태로 둘 앞쪽 문제 개수
 * @property sets 학습 세트 목록
 */
internal data class FakeProject(
    val projectId: String,
    val repositoryName: String,
    val repositoryUrl: String,
    val repositoryImageUrl: String,
    val starCount: Int,
    val techStack: List<String>,
    val presolvedCount: Int,
    val sets: List<FakeSet>,
)

/**
 * 더미 학습 세트다.
 *
 * @property setId 세트 식별자
 * @property label 세트 라벨
 * @property title 세트 제목
 * @property description 세트 설명
 * @property orientation 문제 풀이 전 안내
 * @property level 세트에 걸린 학습 깊이
 * @property questions 문제 목록
 */
internal data class FakeSet(
    val setId: String,
    val label: String,
    val title: String,
    val description: String,
    val orientation: String,
    val level: ProjectQuizLevel,
    val questions: List<FakeQuestion>,
)

/**
 * 정답과 해설을 함께 들고 있는 더미 문제다.
 *
 * @property question 화면에 내려보낼 문제. 정답은 담기지 않는다
 * @property answerIndex 정답 선택지 번호(0부터). 서술형이면 `-1`
 * @property explanation 제출 뒤 보여줄 해설
 */
internal data class FakeQuestion(
    val question: Question,
    val answerIndex: Int,
    val explanation: String,
)

/**
 * 문제가 어느 프로젝트·세트에 속하는지 가리킨다.
 *
 * @property project 문제가 속한 프로젝트
 * @property set 문제가 속한 학습 세트
 * @property question 문제
 */
internal data class FakeQuestionLocation(
    val project: FakeProject,
    val set: FakeSet,
    val question: FakeQuestion,
)
