package com.nexters.hytime.gitit.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import com.nexters.hytime.gitit.designsystem.GitItTheme
import git_it_kmp.shared.generated.resources.Res
import git_it_kmp.shared.generated.resources.intermediate_splash_greeting
import git_it_kmp.shared.generated.resources.intermediate_splash_title_emphasis
import git_it_kmp.shared.generated.resources.intermediate_splash_title_prefix
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

/** 문구가 나타나기 전에 어두운 배경만 유지하는 시간이다. */
private const val INITIAL_BLACK_DURATION_MILLIS = 250L

/** 보조 문구와 핵심 문구의 등장 간격이다. */
private const val CONTENT_STAGGER_MILLIS = 80L

/** 각 문구가 완전히 나타날 때까지의 시간이다. */
private const val FADE_IN_DURATION_MILLIS = 700

/** 문구가 완전히 나타난 뒤 다음 화면으로 넘어가기 전 유지 시간이다. */
private const val CONTENT_HOLD_DURATION_MILLIS = 900L

/**
 * 튜토리얼을 마친 사용자가 홈에 진입하기 전 브랜드 문구를 Fade-in으로 표시한다.
 *
 * @param onFinished 문구 노출이 끝나 다음 화면으로 이동할 때 호출되는 콜백
 * @param modifier 화면 전체의 크기와 배치를 지정할 수식자
 */
@Composable
fun IntermediateSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IntermediateSplashSystemBarsEffect()

    var isGreetingVisible by remember { mutableStateOf(false) }
    var isTitleVisible by remember { mutableStateOf(false) }
    val currentOnFinished by rememberUpdatedState(onFinished)
    val greetingAlpha by
        animateFloatAsState(
            targetValue = if (isGreetingVisible) 1f else 0f,
            animationSpec = tween(FADE_IN_DURATION_MILLIS, easing = FastOutSlowInEasing),
            label = "intermediateSplashGreetingAlpha",
        )
    val titleAlpha by
        animateFloatAsState(
            targetValue = if (isTitleVisible) 1f else 0f,
            animationSpec = tween(FADE_IN_DURATION_MILLIS, easing = FastOutSlowInEasing),
            label = "intermediateSplashTitleAlpha",
        )

    LaunchedEffect(Unit) {
        delay(INITIAL_BLACK_DURATION_MILLIS.milliseconds)
        isGreetingVisible = true
        delay(CONTENT_STAGGER_MILLIS.milliseconds)
        isTitleVisible = true
        delay(FADE_IN_DURATION_MILLIS.toLong().milliseconds + CONTENT_HOLD_DURATION_MILLIS.milliseconds)
        currentOnFinished()
    }

    IntermediateSplashContent(
        greetingAlpha = greetingAlpha,
        titleAlpha = titleAlpha,
        modifier = modifier,
    )
}

/**
 * Figma의 중간 스플래시 레이아웃과 색상 강조를 그린다.
 *
 * @param greetingAlpha 보조 문구의 불투명도
 * @param titleAlpha 핵심 문구의 불투명도
 * @param modifier 화면 전체의 크기와 배치를 지정할 수식자
 */
@Composable
private fun IntermediateSplashContent(
    greetingAlpha: Float,
    titleAlpha: Float,
    modifier: Modifier = Modifier,
) {
    val titlePrefix = stringResource(Res.string.intermediate_splash_title_prefix)
    val titleEmphasis = stringResource(Res.string.intermediate_splash_title_emphasis)
    val currentDensity = LocalDensity.current
    val fixedFontScaleDensity =
        remember(currentDensity.density) {
            Density(
                density = currentDensity.density,
                fontScale = 1f,
            )
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalDensity provides fixedFontScaleDensity) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.intermediate_splash_greeting),
                    modifier = Modifier.alpha(greetingAlpha),
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.splashSubtitle,
                    maxLines = 1,
                )
                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = GitItTheme.colors.grey100)) {
                                append(titlePrefix)
                            }
                            withStyle(SpanStyle(color = GitItTheme.colors.blue200)) {
                                append(titleEmphasis)
                            }
                        },
                    modifier = Modifier.alpha(titleAlpha),
                    style = GitItTheme.typography.splashTitle,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview
@Composable
private fun IntermediateSplashContentPreview() {
    GitItTheme {
        IntermediateSplashContent(
            greetingAlpha = 1f,
            titleAlpha = 1f,
        )
    }
}

/** 중간 화면이 보이는 동안 플랫폼 시스템 바를 어두운 배경에 맞게 조정한다. */
@Composable
internal expect fun IntermediateSplashSystemBarsEffect()
