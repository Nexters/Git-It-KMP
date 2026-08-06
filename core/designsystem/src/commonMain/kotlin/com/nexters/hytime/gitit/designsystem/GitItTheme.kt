package com.nexters.hytime.gitit.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/** Git-it 디자인 토큰과 현재 테마 값을 노출한다. */
object GitItTheme {
    /** Figma 변수 컬렉션에서 가져온 원시 색상 토큰이다. */
    val colors: GitItColors
        get() = defaultGitItColors

    /** Figma 컬러 스타일에서 가져온 그라디언트 토큰이다. */
    val colorStyles: GitItColorStyles
        get() = defaultGitItColorStyles

    /** 현재 컴포지션에 적용된 Git-it 텍스트 스타일 토큰이다. */
    val typography: GitItTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalGitItTypography.current
}

/**
 * Git-it 타이포그래피와 Material 3 테마를 콘텐츠에 적용한다.
 *
 * @param content 디자인 토큰을 적용할 UI 콘텐츠
 */
@Composable
fun GitItTheme(content: @Composable () -> Unit) {
    val typography = createGitItTypography()

    CompositionLocalProvider(LocalGitItTypography provides typography) {
        MaterialTheme(
            typography = typography.toMaterialTypography(),
            content = content,
        )
    }
}
