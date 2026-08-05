package com.nexters.hytime.gitit.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Git-it의 원시 색상 토큰을 제공한다.
 *
 * @property blue500 가장 어두운 브랜드 블루
 * @property blue400 어두운 브랜드 블루
 * @property blue300 중간 브랜드 블루
 * @property blue200 밝은 브랜드 블루
 * @property blue100 가장 밝은 브랜드 블루
 * @property purple500 가장 어두운 브랜드 퍼플
 * @property purple400 어두운 브랜드 퍼플
 * @property purple300 중간 브랜드 퍼플
 * @property purple200 밝은 브랜드 퍼플
 * @property purple100 가장 밝은 브랜드 퍼플
 * @property grey700 가장 어두운 중립색
 * @property grey600 매우 어두운 중립색
 * @property grey500 어두운 중립색
 * @property grey400 중간 중립색
 * @property grey300 밝은 중립색
 * @property grey200 매우 밝은 중립색
 * @property grey100 흰색 중립색
 * @property white15 15% 불투명도의 흰색 오버레이
 * @property white30 30% 불투명도의 흰색 오버레이
 * @property white70 70% 불투명도의 흰색 오버레이
 * @property black70 70% 불투명도의 검은색 오버레이
 * @property error 오류 상태를 나타내는 색상
 * @property caution 주의 상태를 나타내는 색상
 * @property success 성공 상태를 나타내는 색상
 */
@Immutable
class GitItColors internal constructor(
    val blue500: Color,
    val blue400: Color,
    val blue300: Color,
    val blue200: Color,
    val blue100: Color,
    val purple500: Color,
    val purple400: Color,
    val purple300: Color,
    val purple200: Color,
    val purple100: Color,
    val grey700: Color,
    val grey600: Color,
    val grey500: Color,
    val grey400: Color,
    val grey300: Color,
    val grey200: Color,
    val grey100: Color,
    val white15: Color,
    val white30: Color,
    val white70: Color,
    val black70: Color,
    val error: Color,
    val caution: Color,
    val success: Color,
)

/** Figma 변수 컬렉션과 일치하는 기본 색상 토큰이다. */
internal val defaultGitItColors =
    GitItColors(
        blue500 = Color(0xFF2F3853),
        blue400 = Color(0xFF506381),
        blue300 = Color(0xFF7E94BB),
        blue200 = Color(0xFF8BB5EF),
        blue100 = Color(0xFFB9D6FE),
        purple500 = Color(0xFF3B3749),
        purple400 = Color(0xFF585B6F),
        purple300 = Color(0xFF898DA6),
        purple200 = Color(0xFFA4A9C7),
        purple100 = Color(0xFFBDC2DC),
        grey700 = Color(0xFF141414),
        grey600 = Color(0xFF242425),
        grey500 = Color(0xFF3B3B3B),
        grey400 = Color(0xFF919191),
        grey300 = Color(0xFFBCBCBC),
        grey200 = Color(0xFFECECEC),
        grey100 = Color(0xFFFFFFFF),
        white15 = Color.White.copy(alpha = 0.15f),
        white30 = Color.White.copy(alpha = 0.30f),
        white70 = Color.White.copy(alpha = 0.70f),
        black70 = Color.Black.copy(alpha = 0.70f),
        error = Color(0xFFFF3721),
        caution = Color(0xFFECBD23),
        success = Color(0xFF249900),
    )
