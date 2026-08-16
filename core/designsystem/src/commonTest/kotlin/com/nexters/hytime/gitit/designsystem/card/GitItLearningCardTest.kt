package com.nexters.hytime.gitit.designsystem.card

import com.nexters.hytime.gitit.designsystem.defaultGitItColors
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

    /** 카드 배경색별 내부 요소가 Figma 색상 조합으로 매핑되는지 검증한다. */
    @Test
    fun resolveLearningCardColors_backgroundColors_returnsFigmaColors() {
        val colors = defaultGitItColors

        assertEquals(
            LearningCardColors(colors.grey200, colors.purple200, colors.purple400, colors.purple400, colors.grey100),
            resolveLearningCardColors(colors.purple300, colors),
        )
        assertEquals(
            LearningCardColors(colors.grey500, colors.grey200, colors.blue200, colors.blue200, colors.grey500),
            resolveLearningCardColors(colors.blue100, colors),
        )
        assertEquals(
            LearningCardColors(colors.grey400, colors.purple300, colors.blue400, colors.blue400, colors.grey100),
            resolveLearningCardColors(colors.blue500, colors),
        )
    }
}
