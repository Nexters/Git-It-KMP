package com.nexters.hytime.gitit.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals

/** 홈 카드 앵글 계산을 검증한다. */
class HomeScreenTest {
    /** 현재 카드와 다음 카드가 Figma에 정의된 각도를 사용한다. */
    @Test
    fun learningCardAngle_currentAndAdjacent_returnsFigmaAngles() {
        assertEquals(0f, learningCardAngle(page = 0, pageOffset = 0f))
        assertEquals(16f, learningCardAngle(page = 1, pageOffset = 1f))
        assertEquals(-12f, learningCardAngle(page = 2, pageOffset = 1f))
    }

    /** 스와이프 중인 카드는 페이지 진행도만큼 각도가 연속으로 줄어든다. */
    @Test
    fun learningCardAngle_duringSwipe_interpolatesWithPageOffset() {
        assertEquals(8f, learningCardAngle(page = 1, pageOffset = 0.5f))
    }
}
