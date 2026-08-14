package com.nexters.hytime.gitit.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import git_it_kmp.core.designsystem.generated.resources.Res
import git_it_kmp.core.designsystem.generated.resources.noto_sans_kr_variable
import git_it_kmp.core.designsystem.generated.resources.plus_jakarta_sans_variable
import org.jetbrains.compose.resources.Font

/**
 * Git-it의 텍스트 스타일 토큰을 제공한다.
 *
 * @property splashTitle 중간 스플래시의 핵심 문구에 사용하는 44sp 굵은 스타일
 * @property splashSubtitle 중간 스플래시의 보조 문구에 사용하는 24sp 세미볼드 스타일
 * @property headline1 가장 큰 화면 제목에 사용하는 30sp 굵은 스타일
 * @property headline2 두 번째 수준 화면 제목에 사용하는 28sp 굵은 스타일
 * @property subtitle1 가장 큰 부제목에 사용하는 24sp 굵은 스타일
 * @property subtitle2 중간 부제목에 사용하는 18sp 굵은 스타일
 * @property subtitle3 작은 부제목에 사용하는 16sp 굵은 스타일
 * @property body1 강조 본문에 사용하는 16sp 중간 굵기 스타일
 * @property body2 기본 본문에 사용하는 14sp 중간 굵기 스타일
 * @property body3 보조 본문에 사용하는 12sp 중간 굵기 스타일
 * @property caption1 기본 캡션에 사용하는 12sp 보통 굵기 스타일
 * @property caption2 가장 작은 캡션에 사용하는 10sp 중간 굵기 스타일
 */
@Immutable
class GitItTypography internal constructor(
    val splashTitle: TextStyle,
    val splashSubtitle: TextStyle,
    val headline1: TextStyle,
    val headline2: TextStyle,
    val subtitle1: TextStyle,
    val subtitle2: TextStyle,
    val subtitle3: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val caption1: TextStyle,
    val caption2: TextStyle,
)

/** 현재 컴포지션에서 사용하는 Git-it 타이포그래피 토큰이다. */
internal val LocalGitItTypography =
    staticCompositionLocalOf<GitItTypography> {
        error("GitItTheme 안에서 타이포그래피를 사용해야 합니다.")
    }

/**
 * 언어별 글리프 폴백과 Figma 규격을 반영한 기본 타이포그래피를 만든다.
 *
 * @return 영문은 Plus Jakarta Sans, 한글은 Noto Sans KR로 렌더링하는 텍스트 스타일 모음
 */
@Composable
internal fun createGitItTypography(): GitItTypography {
    val fontFamily = createGitItFontFamily()

    return GitItTypography(
        splashTitle = textStyle(fontFamily, FontWeight.Bold, 44, 61.6f, -0.88f),
        splashSubtitle = textStyle(fontFamily, FontWeight.SemiBold, 24, 33.6f),
        headline1 = textStyle(fontFamily, FontWeight.Bold, 30, 37.2f),
        headline2 = textStyle(fontFamily, FontWeight.Bold, 28, 36.4f),
        subtitle1 = textStyle(fontFamily, FontWeight.Bold, 24, 35.52f),
        subtitle2 = textStyle(fontFamily, FontWeight.Bold, 18, 26.64f),
        subtitle3 = textStyle(fontFamily, FontWeight.Bold, 16, 23.68f),
        body1 = textStyle(fontFamily, FontWeight.Medium, 16, 24f),
        body2 = textStyle(fontFamily, FontWeight.Medium, 14, 21f),
        body3 = textStyle(fontFamily, FontWeight.Medium, 12, 18f),
        caption1 = textStyle(fontFamily, FontWeight.Normal, 12, 18f),
        caption2 = textStyle(fontFamily, FontWeight.Medium, 10, 15f),
    )
}

/**
 * 영문 글리프를 우선 처리하고 한글 글리프를 폴백하는 폰트 패밀리를 만든다.
 *
 * @return 보통·중간·세미볼드·굵은 굵기를 지원하는 다국어 폰트 패밀리
 */
@Composable
private fun createGitItFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.plus_jakarta_sans_variable, FontWeight.Normal),
        Font(Res.font.noto_sans_kr_variable, FontWeight.Normal),
        Font(Res.font.plus_jakarta_sans_variable, FontWeight.Medium),
        Font(Res.font.noto_sans_kr_variable, FontWeight.Medium),
        Font(Res.font.plus_jakarta_sans_variable, FontWeight.SemiBold),
        Font(Res.font.noto_sans_kr_variable, FontWeight.SemiBold),
        Font(Res.font.plus_jakarta_sans_variable, FontWeight.Bold),
        Font(Res.font.noto_sans_kr_variable, FontWeight.Bold),
    )

/**
 * Figma 텍스트 속성을 Compose 텍스트 스타일로 변환한다.
 *
 * @param fontFamily 영문과 한글 글리프를 처리할 폰트 패밀리
 * @param fontWeight 텍스트 굵기
 * @param fontSize 텍스트 크기(sp)
 * @param lineHeight 줄 높이(sp)
 * @param letterSpacing 글자 사이의 간격(sp)
 * @return Figma의 크기와 자간을 반영한 Compose 텍스트 스타일
 */
private fun textStyle(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontSize: Int,
    lineHeight: Float,
    letterSpacing: Float = 0f,
): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = letterSpacing.sp,
    )

/**
 * 사용자 정의 텍스트 토큰을 Material 3의 타이포그래피 슬롯에 연결한다.
 *
 * @return 모든 Material 3 텍스트 슬롯이 Git-it 폰트를 사용하는 타이포그래피
 */
internal fun GitItTypography.toMaterialTypography(): Typography =
    Typography(
        displayLarge = headline1,
        displayMedium = headline1,
        displaySmall = headline1,
        headlineLarge = headline1,
        headlineMedium = headline2,
        headlineSmall = subtitle1,
        titleLarge = subtitle2,
        titleMedium = subtitle3,
        titleSmall = subtitle3,
        bodyLarge = body1,
        bodyMedium = body2,
        bodySmall = body3,
        labelLarge = caption1,
        labelMedium = caption1,
        labelSmall = caption2,
    )
