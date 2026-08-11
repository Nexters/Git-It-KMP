package com.nexters.hytime.gitit.feature.my

import kotlin.test.Test
import kotlin.test.assertEquals

/** 주간 학습량 차트의 세로축 계산을 검증한다. */
class MyScreenTest {
    /** 기본 범위와 20 단위 경계에서 세로축 최댓값이 올바르게 확장되는지 검증한다. */
    @Test
    fun weeklyStudyAxisMax_boundaryValues_expandsInTwentyUnitSteps() {
        assertEquals(20, weeklyStudyAxisMax(-1))
        assertEquals(20, weeklyStudyAxisMax(0))
        assertEquals(20, weeklyStudyAxisMax(20))
        assertEquals(40, weeklyStudyAxisMax(21))
        assertEquals(40, weeklyStudyAxisMax(40))
        assertEquals(60, weeklyStudyAxisMax(41))
    }
}
