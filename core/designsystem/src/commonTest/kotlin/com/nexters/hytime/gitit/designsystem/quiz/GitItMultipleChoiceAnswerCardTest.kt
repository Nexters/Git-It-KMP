package com.nexters.hytime.gitit.designsystem.quiz

import androidx.compose.ui.graphics.toArgb
import kotlin.test.Test
import kotlin.test.assertEquals

/** 객관식 답안 카드 상태와 Figma 변형의 매핑을 검증한다. */
class GitItMultipleChoiceAnswerCardTest {
    /** 여덟 상태가 Figma의 배경색·내용·chevron 규격과 일치하는지 검증한다. */
    @Test
    fun state_matchesFigmaVariants() {
        val actual =
            GitItMultipleChoiceAnswerState.entries.map { state ->
                listOf(
                    state.backgroundColor.toArgb().toUInt(),
                    state.showsAnswer,
                    state.chevronExpanded,
                    state.isToggleState,
                    state == GitItMultipleChoiceAnswerState.Selected,
                )
            }

        assertEquals(
            expected =
                listOf(
                    listOf(0xFF242425u, true, null, false, false),
                    listOf(0xFF242425u, true, null, false, true),
                    listOf(0xFF242425u, false, false, true, false),
                    listOf(0xFF242425u, true, true, true, false),
                    listOf(0xFFFF5656u, true, true, true, false),
                    listOf(0xFFFF5656u, false, false, true, false),
                    listOf(0xFF3E85FFu, true, true, true, false),
                    listOf(0xFF3E85FFu, false, false, true, false),
                ),
            actual = actual,
        )
    }
}
