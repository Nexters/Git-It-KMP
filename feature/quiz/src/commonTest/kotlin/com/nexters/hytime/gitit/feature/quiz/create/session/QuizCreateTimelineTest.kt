package com.nexters.hytime.gitit.feature.quiz.create.session

import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateStep
import kotlin.test.Test
import kotlin.test.assertEquals

/** 생성 진행 시뮬레이션의 단계별 시간 비중을 검증한다. */
class QuizCreateTimelineTest {
    /** 각 누적 비중 경계에서 다음 생성 단계로 전환한다. */
    @Test
    fun progressSnapshot_weightBoundaries_mapsExpectedSteps() {
        assertStep(0, QuizCreateStep.ProjectInfo)
        assertStep(2, QuizCreateStep.ProjectInfo)
        assertStep(3, QuizCreateStep.CodeStructure)
        assertStep(37, QuizCreateStep.CodeStructure)
        assertStep(38, QuizCreateStep.LearningConcepts)
        assertStep(54, QuizCreateStep.LearningConcepts)
        assertStep(55, QuizCreateStep.Questions)
        assertStep(84, QuizCreateStep.Questions)
        assertStep(85, QuizCreateStep.Validation)
        assertStep(98, QuizCreateStep.Validation)
    }

    /** 서버 완료 신호 전에는 전체 시간이 지나도 최대 98%까지만 표시한다. */
    @Test
    fun progressSnapshot_elapsedAtOrAfterTotal_clampsToNinetyEightPercent() {
        val atEnd = quizCreateProgressSnapshot(TOTAL_DURATION_MILLIS, TOTAL_DURATION_MILLIS)
        val afterEnd = quizCreateProgressSnapshot(TOTAL_DURATION_MILLIS * 2, TOTAL_DURATION_MILLIS)

        assertEquals(98, atEnd.progressPercent)
        assertEquals(98, afterEnd.progressPercent)
        assertEquals(QuizCreateStep.Validation, atEnd.step)
    }

    /**
     * 지정한 전체 진행률에서 계산되는 단계와 진행률을 검증한다.
     *
     * @param progressPercent 전체 시간에서 경과시킬 진행률
     * @param expected 해당 진행률에서 기대하는 생성 단계
     */
    private fun assertStep(
        progressPercent: Int,
        expected: QuizCreateStep,
    ) {
        val snapshot =
            quizCreateProgressSnapshot(
                elapsedMillis = progressPercent * ONE_PERCENT_MILLIS,
                totalDurationMillis = TOTAL_DURATION_MILLIS,
            )

        assertEquals(expected, snapshot.step)
        assertEquals(progressPercent, snapshot.progressPercent)
    }

    private companion object {
        const val ONE_PERCENT_MILLIS = 1_000L
        const val TOTAL_DURATION_MILLIS = 100 * ONE_PERCENT_MILLIS
    }
}
