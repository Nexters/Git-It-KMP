package com.nexters.hytime.gitit.designsystem

import androidx.compose.ui.graphics.toArgb
import kotlin.test.Test
import kotlin.test.assertContentEquals

/** Figma 원시 색상과 Compose 토큰 사이의 매핑을 검증한다. */
class ColorTokensTest {
    /** 모든 불투명 색상 토큰이 Figma의 ARGB 값과 일치하는지 검증한다. */
    @Test
    fun colors_matchFigmaRawValues() {
        val actual =
            listOf(
                defaultGitItColors.blue500,
                defaultGitItColors.blue400,
                defaultGitItColors.blue300,
                defaultGitItColors.blue200,
                defaultGitItColors.blue100,
                defaultGitItColors.purple500,
                defaultGitItColors.purple400,
                defaultGitItColors.purple300,
                defaultGitItColors.purple200,
                defaultGitItColors.purple100,
                defaultGitItColors.grey700,
                defaultGitItColors.grey600,
                defaultGitItColors.grey500,
                defaultGitItColors.grey400,
                defaultGitItColors.grey300,
                defaultGitItColors.grey200,
                defaultGitItColors.grey100,
                defaultGitItColors.error,
                defaultGitItColors.caution,
                defaultGitItColors.success,
            ).map { it.toArgb().toUInt() }

        assertContentEquals(
            expected =
                listOf(
                    0xFF2F3853u,
                    0xFF506381u,
                    0xFF7E94BBu,
                    0xFF8BB5EFu,
                    0xFFB9D6FEu,
                    0xFF3B3749u,
                    0xFF585B6Fu,
                    0xFF898DA6u,
                    0xFFA4A9C7u,
                    0xFFBDC2DCu,
                    0xFF141414u,
                    0xFF242425u,
                    0xFF3B3B3Bu,
                    0xFF919191u,
                    0xFFBCBCBCu,
                    0xFFECECECu,
                    0xFFFFFFFFu,
                    0xFFFF3721u,
                    0xFFECBD23u,
                    0xFF249900u,
                ),
            actual = actual,
        )
    }

    /** 오버레이 토큰이 Figma에서 정의한 불투명도를 ARGB 알파로 보존하는지 검증한다. */
    @Test
    fun opacityColors_matchFigmaAlphaValues() {
        val actual =
            listOf(
                defaultGitItColors.white05,
                defaultGitItColors.white15,
                defaultGitItColors.white30,
                defaultGitItColors.white70,
                defaultGitItColors.black70,
            ).map { it.toArgb().toUInt() }

        assertContentEquals(
            expected = listOf(0x0DFFFFFFu, 0x26FFFFFFu, 0x4DFFFFFFu, 0xB3FFFFFFu, 0xB3000000u),
            actual = actual,
        )
    }
}
