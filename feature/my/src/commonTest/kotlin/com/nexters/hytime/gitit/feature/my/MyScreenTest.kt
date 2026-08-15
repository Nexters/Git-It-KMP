package com.nexters.hytime.gitit.feature.my

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
}
