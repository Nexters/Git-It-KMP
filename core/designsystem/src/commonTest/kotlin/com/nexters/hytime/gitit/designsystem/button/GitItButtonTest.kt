@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.designsystem.button

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.defaultGitItColors
import kotlin.test.Test
import kotlin.test.assertEquals

/** Figma Button 속성과 Compose 규격 사이의 매핑을 검증한다. */
class GitItButtonTest {
    /** 버튼 크기별 높이와 수평 여백이 Figma 규격과 일치하는지 검증한다. */
    @Test
    fun size_모든규격_피그마치수와일치한다() {
        assertEquals(54.dp to 12.dp, GitItButtonSize.Large.height to GitItButtonSize.Large.horizontalPadding)
        assertEquals(40.dp to 10.dp, GitItButtonSize.Medium.height to GitItButtonSize.Medium.horizontalPadding)
        assertEquals(36.dp to 8.dp, GitItButtonSize.Small.height to GitItButtonSize.Small.horizontalPadding)
    }

    /** Default 버튼을 누르는 동안 Figma Active 상태로 전환하는지 검증한다. */
    @Test
    fun visualState_default버튼을누르면_active를반환한다() {
        assertEquals(
            expected = GitItButtonState.Active,
            actual =
                resolveButtonVisualState(
                    state = GitItButtonState.Default,
                    isPressed = true,
                ),
        )
    }

    /** 명시적인 피드백 상태는 누름 여부로 덮어쓰지 않는지 검증한다. */
    @Test
    fun visualState_default가아닌상태를누르면_기존상태를유지한다() {
        val actual =
            listOf(
                GitItButtonState.Active,
                GitItButtonState.Disabled,
                GitItButtonState.Error,
            ).map { state ->
                resolveButtonVisualState(
                    state = state,
                    isPressed = true,
                )
            }

        assertEquals(
            expected =
                listOf(
                    GitItButtonState.Active,
                    GitItButtonState.Disabled,
                    GitItButtonState.Error,
                ),
            actual = actual,
        )
    }

    /** 모든 스타일과 상태 조합의 배경색이 Figma 변수와 일치하는지 검증한다. */
    @Test
    fun containerColor_모든스타일과상태_피그마변수와일치한다() {
        val actual =
            GitItButtonStyle.entries.flatMap { style ->
                GitItButtonState.entries.map { state ->
                    resolveButtonColors(
                        style = style,
                        state = state,
                        colors = defaultGitItColors,
                    ).containerColor.toHex()
                }
            }

        assertEquals(
            expected =
                listOf(
                    0xFFB9D6FEu,
                    0xFFCEE2FEu,
                    0x26FFFFFFu,
                    0xFFFF3721u,
                    0x26FFFFFFu,
                    0x26FFFFFFu,
                    0x26FFFFFFu,
                    0x26FFFFFFu,
                    0x00000000u,
                    0x26FFFFFFu,
                    0x00000000u,
                    0x00000000u,
                    0x00000000u,
                    0x26FFFFFFu,
                    0x00000000u,
                    0x00000000u,
                ),
            actual = actual,
        )
    }

    /** 모든 스타일과 상태 조합의 콘텐츠 색상이 Figma 변수와 일치하는지 검증한다. */
    @Test
    fun contentColor_모든스타일과상태_피그마변수와일치한다() {
        val actual =
            GitItButtonStyle.entries.flatMap { style ->
                GitItButtonState.entries.map { state ->
                    resolveButtonColors(
                        style = style,
                        state = state,
                        colors = defaultGitItColors,
                    ).contentColor.toHex()
                }
            }

        assertEquals(
            expected =
                listOf(
                    0xFF141414u,
                    0xFF141414u,
                    0x4DFFFFFFu,
                    0xFFFFFFFFu,
                    0xFFFFFFFFu,
                    0xFFFFFFFFu,
                    0x4DFFFFFFu,
                    0xFFFF3721u,
                    0xFFB9D6FEu,
                    0xFFB9D6FEu,
                    0xFF506381u,
                    0xFFFF3721u,
                    0xFFFFFFFFu,
                    0xFFFFFFFFu,
                    0x4DFFFFFFu,
                    0xFFFF3721u,
                ),
            actual = actual,
        )
    }
}

/**
 * 색상을 테스트 비교에 사용할 ARGB 부호 없는 정수로 변환한다.
 *
 * @return Compose 색상의 32비트 ARGB 값
 */
private fun Color.toHex(): UInt = toArgb().toUInt()
