package com.nexters.hytime.gitit.designsystem.card

import kotlin.test.Test
import kotlin.test.assertEquals

/** 학습 카드의 표시 값 보정을 검증한다. */
class GitItLearningCardTest {
    /** 진행률이 트랙을 벗어나지 않도록 0f..1f 범위로 제한되는지 검증한다. */
    @Test
    fun normalizedProgress_outOfRange_clampsToTrack() {
        assertEquals(0f, normalizedProgress(-0.1f))
        assertEquals(0.5f, normalizedProgress(0.5f))
        assertEquals(1f, normalizedProgress(1.1f))
        assertEquals(0f, normalizedProgress(Float.NaN))
    }
}
