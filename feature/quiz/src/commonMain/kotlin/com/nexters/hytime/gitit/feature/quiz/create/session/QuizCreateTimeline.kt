package com.nexters.hytime.gitit.feature.quiz.create.session

import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateStep
import kotlin.random.Random

/**
 * 서버 진행률 대신 사용자에게 표시할 문제 생성 타임라인을 재생한다.
 *
 * 실제 생성 요청의 성공 여부와 무관하게 3~5분 동안 단계별 진행 상태를 자연스럽게 보여주기 위한 UI 전용 실행기다.
 */
internal object QuizCreateTimeline {
    /** 새 생성 세션에 사용할 3~5분 사이의 전체 시간을 반환한다. */
    fun randomDurationMillis(): Long = Random.nextLong(MIN_DURATION_MILLIS, MAX_DURATION_MILLIS + 1)

    private const val MIN_DURATION_MILLIS = 3 * 60 * 1_000L
    private const val MAX_DURATION_MILLIS = 5 * 60 * 1_000L
}

/**
 * 특정 경과 시간에 표시할 생성 단계와 진행률을 계산한다.
 *
 * @param elapsedMillis 생성 시작 후 경과 시간
 * @param totalDurationMillis 이번 생성 타임라인의 전체 시간
 * 서버 완료 신호가 오기 전에는 전체 시간이 지나도 98%에서 대기한다.
 *
 * @return Figma 진행 화면에 표시할 단계와 0..98 진행률
 */
internal fun quizCreateProgressSnapshot(
    elapsedMillis: Long,
    totalDurationMillis: Long,
): QuizCreateProgressSnapshot {
    require(totalDurationMillis > 0) { "전체 생성 시간은 0보다 커야 합니다." }

    val progressPercent =
        ((elapsedMillis.coerceIn(0L, totalDurationMillis) * 100) / totalDurationMillis)
            .toInt()
            .coerceAtMost(WAITING_PROGRESS_PERCENT)
    val step =
        when {
            progressPercent < PROJECT_INFO_END_PERCENT -> QuizCreateStep.ProjectInfo
            progressPercent < CODE_STRUCTURE_END_PERCENT -> QuizCreateStep.CodeStructure
            progressPercent < LEARNING_CONCEPTS_END_PERCENT -> QuizCreateStep.LearningConcepts
            progressPercent < QUESTIONS_END_PERCENT -> QuizCreateStep.Questions
            else -> QuizCreateStep.Validation
        }

    return QuizCreateProgressSnapshot(step = step, progressPercent = progressPercent)
}

/**
 * 시뮬레이션 타임라인의 한 시점이다.
 *
 * @property step 현재 표시할 생성 단계
 * @property progressPercent 전체 타임라인 진행률
 */
internal data class QuizCreateProgressSnapshot(
    val step: QuizCreateStep,
    val progressPercent: Int,
)

private const val PROJECT_INFO_END_PERCENT = 3
private const val CODE_STRUCTURE_END_PERCENT = 38
private const val LEARNING_CONCEPTS_END_PERCENT = 55
private const val QUESTIONS_END_PERCENT = 85
private const val WAITING_PROGRESS_PERCENT = 98
