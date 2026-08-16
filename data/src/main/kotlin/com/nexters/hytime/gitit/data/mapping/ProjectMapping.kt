package com.nexters.hytime.gitit.data.mapping

import com.nexters.hytime.gitit.data.dto.AvailableProjectResponse
import com.nexters.hytime.gitit.data.dto.BookmarkedQuestionListResponse
import com.nexters.hytime.gitit.data.dto.BookmarkedQuestionResponse
import com.nexters.hytime.gitit.data.dto.LearningSetResponse
import com.nexters.hytime.gitit.data.dto.MyAnswerResponse
import com.nexters.hytime.gitit.data.dto.ProjectDetailResponse
import com.nexters.hytime.gitit.data.dto.ProjectItemResponse
import com.nexters.hytime.gitit.data.dto.ProjectListResponse
import com.nexters.hytime.gitit.data.dto.QuestionResponse
import com.nexters.hytime.gitit.data.dto.RubricCriterionResponse
import com.nexters.hytime.gitit.data.dto.RubricResponse
import com.nexters.hytime.gitit.data.dto.SetResponse
import com.nexters.hytime.gitit.data.dto.SourceResponse
import com.nexters.hytime.gitit.data.dto.SubmitChoiceAnswerResponse
import com.nexters.hytime.gitit.data.dto.SubmitEssayAnswerResponse
import com.nexters.hytime.gitit.domain.model.AvailableProject
import com.nexters.hytime.gitit.domain.model.BookmarkedQuestion
import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.model.EssayAnswerResult
import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.LearningSetSummary
import com.nexters.hytime.gitit.domain.model.MyAnswer
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectSummary
import com.nexters.hytime.gitit.domain.model.Question
import com.nexters.hytime.gitit.domain.model.QuestionFormat
import com.nexters.hytime.gitit.domain.model.QuestionSource
import com.nexters.hytime.gitit.domain.model.Rubric
import com.nexters.hytime.gitit.domain.model.RubricCriterion

/**
 * 프로젝트 목록 응답을 도메인 페이지로 변환한다.
 *
 * @return 네트워크 표현을 제거한 프로젝트 한 페이지
 */
internal fun ProjectListResponse.toDomain(): ProjectPage =
    ProjectPage(
        items = items.map(ProjectItemResponse::toDomain),
        hasNext = hasNext,
    )

/**
 * 목록 항목 응답을 도메인 요약으로 변환한다.
 *
 * @return 목록 화면이 사용할 프로젝트 요약
 */
internal fun ProjectItemResponse.toDomain(): ProjectSummary =
    ProjectSummary(
        projectId = projectId,
        repositoryName = repositoryName,
        repositoryImageUrl = repositoryImageUrl,
        techStack = techStack,
        currentSetLabel = currentSetLabel,
        currentSetTitle = currentSetTitle,
        nextProblemId = nextProblemId,
        overallProgressPercent = overallProgressPercent,
    )

/**
 * 프로젝트 상세 응답을 도메인 모델로 변환한다.
 *
 * @return 상세 화면이 사용할 프로젝트 정보
 */
internal fun ProjectDetailResponse.toDomain(): ProjectDetail =
    ProjectDetail(
        projectId = projectId,
        repositoryUrl = repositoryUrl,
        repositoryName = repositoryName,
        repositoryImageUrl = repositoryImageUrl,
        starCount = starCount,
        techStack = techStack,
        overallProgressPercent = overallProgressPercent,
        nextProblemId = nextProblemId,
        sets = sets.map(SetResponse::toDomain),
    )

/**
 * 세트 응답을 도메인 요약으로 변환한다.
 *
 * @return 상세 화면에 나열할 학습 세트 요약
 */
internal fun SetResponse.toDomain(): LearningSetSummary =
    LearningSetSummary(
        setId = setId,
        label = label,
        title = title,
        problemCount = problemCount,
        completedCount = completedCount,
    )

/**
 * 학습 세트 응답을 도메인 모델로 변환한다.
 *
 * @return 문제 풀이 화면이 사용할 학습 세트
 */
internal fun LearningSetResponse.toDomain(): LearningSet =
    LearningSet(
        setId = setId,
        title = title,
        description = description,
        orientation = orientation,
        level = ProjectQuizLevel.entries.firstOrNull { it.name == level },
        questions = questions.map(QuestionResponse::toDomain),
    )

/**
 * 문제 응답을 도메인 모델로 변환한다.
 *
 * @return 화면에 표시할 문제
 */
internal fun QuestionResponse.toDomain(): Question =
    Question(
        questionId = questionId,
        format = QuestionFormat.entries.firstOrNull { it.name == format },
        text = text,
        choices = choices,
        sources = sources.map(SourceResponse::toDomain),
        myAnswer = myAnswer?.toDomain(),
    )

/**
 * 코드 위치 응답을 도메인 모델로 변환한다.
 *
 * @return 문제가 인용한 코드 위치
 */
internal fun SourceResponse.toDomain(): QuestionSource =
    QuestionSource(
        file = file,
        startLine = startLine,
        endLine = endLine,
        symbol = symbol,
        summary = summary,
        url = url,
    )

/**
 * 제출한 답 응답을 도메인 모델로 변환한다.
 *
 * @return 이미 제출한 답
 */
internal fun MyAnswerResponse.toDomain(): MyAnswer =
    MyAnswer(
        selectedIndex = selectedIndex,
        text = text,
        correct = correct,
        answeredAt = answeredAt,
    )

/**
 * 4지선다 제출 응답을 도메인 모델로 변환한다.
 *
 * @return 정답 여부와 해설
 */
internal fun SubmitChoiceAnswerResponse.toDomain(): ChoiceAnswerResult =
    ChoiceAnswerResult(
        questionId = questionId,
        correct = correct,
        answerIndex = answerIndex,
        explanation = explanation,
    )

/**
 * 서술형 제출 응답을 도메인 모델로 변환한다.
 *
 * @return 해설과 자가채점 기준
 */
internal fun SubmitEssayAnswerResponse.toDomain(): EssayAnswerResult =
    EssayAnswerResult(
        questionId = questionId,
        explanation = explanation,
        rubric = rubric.toDomain(),
    )

/**
 * 채점 기준 응답을 도메인 모델로 변환한다.
 *
 * @return 자가채점 기준
 */
internal fun RubricResponse.toDomain(): Rubric =
    Rubric(
        criteria = criteria.map(RubricCriterionResponse::toDomain),
        keyPoints = keyPoints,
        fullMarkExample = fullMarkExample,
        partialExample = partialExample,
        zeroExample = zeroExample,
    )

/**
 * 채점 기준 항목 응답을 도메인 모델로 변환한다.
 *
 * @return 판단 기준과 배점
 */
internal fun RubricCriterionResponse.toDomain(): RubricCriterion =
    RubricCriterion(
        text = text,
        points = points,
    )

/**
 * 북마크 목록 응답을 도메인 모델로 변환한다.
 *
 * @return 저장한 문제 화면이 사용할 북마크 목록
 */
internal fun BookmarkedQuestionListResponse.toDomain(): BookmarkedQuestions =
    BookmarkedQuestions(
        totalCount = totalCount,
        availableProjects = availableProjects.map(AvailableProjectResponse::toDomain),
        bookmarks = bookmarks.map(BookmarkedQuestionResponse::toDomain),
    )

/**
 * 북마크 필터 프로젝트 응답을 도메인 모델로 변환한다.
 *
 * @return 필터에 노출할 프로젝트
 */
internal fun AvailableProjectResponse.toDomain(): AvailableProject =
    AvailableProject(
        projectId = projectId,
        projectName = projectName,
    )

/**
 * 북마크한 문제 응답을 도메인 모델로 변환한다.
 *
 * @return 북마크한 문제
 */
internal fun BookmarkedQuestionResponse.toDomain(): BookmarkedQuestion =
    BookmarkedQuestion(
        projectId = projectId,
        projectName = projectName,
        setLabel = setLabel,
        problemNumber = problemNumber,
        questionId = questionId,
        question = question,
    )
