package com.nexters.hytime.gitit.feature.my

import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.my_weekly_study_complete
import git_it_kmp.feature.my.generated.resources.my_weekly_study_empty
import git_it_kmp.feature.my.generated.resources.my_weekly_study_in_progress
import kotlin.test.Test
import kotlin.test.assertEquals

/** 주간 학습량 차트의 막대 높이 계산을 검증한다. */
class MyScreenTest {
    /** 문제 수가 유효 범위를 벗어나도 막대 높이가 0부터 81 사이로 제한되는지 검증한다. */
    @Test
    fun weeklyStudyBarHeight_boundaryValues_clampsToChartRange() {
        assertEquals(0f, weeklyStudyBarHeight(-1, 10))
        assertEquals(0f, weeklyStudyBarHeight(5, 0))
        assertEquals(40.5f, weeklyStudyBarHeight(5, 10))
        assertEquals(81f, weeklyStudyBarHeight(10, 10))
        assertEquals(81f, weeklyStudyBarHeight(11, 10))
    }

    /** 입력 순서와 누락 여부와 관계없이 월요일부터 일요일까지 표시하는지 검증한다. */
    @Test
    fun fixedWeeklyStudyItems_unorderedAndMissingDays_returnsMondayToSunday() {
        val items =
            listOf(
                MyWeeklyStudy(day = "일", solvedCount = 7),
                MyWeeklyStudy(day = "월", solvedCount = 1),
                MyWeeklyStudy(day = "수", solvedCount = 3),
            )

        val result = fixedWeeklyStudyItems(items)

        assertEquals(listOf("월", "화", "수", "목", "금", "토", "일"), result.map(MyWeeklyStudy::day))
        assertEquals(listOf(1, 0, 3, 0, 0, 0, 7), result.map(MyWeeklyStudy::solvedCount))
    }

    /** 학습한 날짜 수에 따라 시작·진행·완료 안내 문구 리소스를 반환하는지 검증한다. */
    @Test
    fun weeklyStudyMessageResource_learningDays_returnsMatchingResource() {
        assertEquals(Res.string.my_weekly_study_empty, weeklyStudyMessageResource(studyItems(0, 0, 0, 0, 0, 0, 0)))
        assertEquals(Res.string.my_weekly_study_in_progress, weeklyStudyMessageResource(studyItems(1, 0, 0, 0, 0, 0, 0)))
        assertEquals(Res.string.my_weekly_study_complete, weeklyStudyMessageResource(studyItems(1, 1, 1, 1, 1, 1, 1)))
    }

    /**
     * 테스트용 주간 학습량을 만든다.
     *
     * @param solvedCounts 월요일부터 순서대로 적용할 문제 수
     * @return 전달한 문제 수를 갖는 주간 학습량
     */
    private fun studyItems(vararg solvedCounts: Int): List<MyWeeklyStudy> =
        solvedCounts.mapIndexed { index, solvedCount ->
            MyWeeklyStudy(day = index.toString(), solvedCount = solvedCount)
        }
}
