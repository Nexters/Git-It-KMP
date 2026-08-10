@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.designsystem.selectcard

import androidx.compose.ui.graphics.Color
import com.nexters.hytime.gitit.designsystem.defaultGitItColors
import kotlin.test.Test
import kotlin.test.assertEquals

/** Select Card 선택 상태와 Figma 색상 토큰의 매핑을 검증한다. */
class GitItSelectCardTest {
    /** 선택되지 않은 카드는 외곽선을 표시하지 않고 기본 콘텐츠 색상을 유지하는지 검증한다. */
    @Test
    fun resolveColors_unselected_returnsDefaultColors() {
        val actual = resolveSelectCardColors(selected = false, colors = defaultGitItColors)

        assertEquals(defaultGitItColors.grey600, actual.containerColor)
        assertEquals(Color.Transparent, actual.borderColor)
        assertEquals(defaultGitItColors.grey100, actual.titleColor)
        assertEquals(defaultGitItColors.grey300, actual.descriptionColor)
        assertEquals(defaultGitItColors.blue500, actual.tagContainerColor)
        assertEquals(defaultGitItColors.blue200, actual.tagContentColor)
    }

    /** 선택된 카드는 Figma의 blue200 외곽선을 사용하는지 검증한다. */
    @Test
    fun resolveColors_selected_returnsBlueOutline() {
        val actual = resolveSelectCardColors(selected = true, colors = defaultGitItColors)

        assertEquals(defaultGitItColors.blue200, actual.borderColor)
    }
}
