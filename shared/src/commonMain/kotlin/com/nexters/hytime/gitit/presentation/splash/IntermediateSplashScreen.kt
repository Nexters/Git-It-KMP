package com.nexters.hytime.gitit.presentation.splash

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import git_it_kmp.shared.generated.resources.Res
import git_it_kmp.shared.generated.resources.intermediate_splash_greeting
import git_it_kmp.shared.generated.resources.intermediate_splash_title_emphasis
import git_it_kmp.shared.generated.resources.intermediate_splash_title_prefix
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

/** 첫 글자를 입력하기 전에 커서만 노출하는 시간이다. */
private const val INITIAL_CURSOR_DURATION_MILLIS = 200L

/** 보조 문구에서 글자 하나를 입력하는 간격이다. */
private const val GREETING_CHARACTER_DELAY_MILLIS = 72L

/** 핵심 문구에서 글자 하나를 입력하는 간격이다. */
private const val TITLE_CHARACTER_DELAY_MILLIS = 62L

/** 보조 문구 입력 후 커서를 깜빡이는 시간이다. */
private const val GREETING_CURSOR_HOLD_MILLIS = 500L

/** 입력을 마친 뒤 다음 화면으로 넘어가기 전 커서를 깜빡이는 시간이다. */
private const val CONTENT_HOLD_DURATION_MILLIS = 900L

/** 커서가 한 번 깜빡이는 시간이다. */
private const val CURSOR_BLINK_DURATION_MILLIS = 550

/** 타이핑 애니메이션에서 현재 입력 중인 문구를 나타낸다. */
private enum class TypingStage {
    GREETING,
    GREETING_PAUSE,
    TITLE,
    TITLE_PAUSE,
}

/**
 * 튜토리얼을 마친 사용자가 홈에 진입하기 전 브랜드 문구를 타이핑 효과로 표시한다.
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

    val greeting = stringResource(Res.string.intermediate_splash_greeting)
    val titlePrefix = stringResource(Res.string.intermediate_splash_title_prefix)
    val titleEmphasis = stringResource(Res.string.intermediate_splash_title_emphasis)
    var greetingCharacterCount by remember { mutableStateOf(0) }
    var titleCharacterCount by remember { mutableStateOf(0) }
    var typingStage by remember { mutableStateOf(TypingStage.GREETING) }
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(greeting, titlePrefix, titleEmphasis) {
        delay(INITIAL_CURSOR_DURATION_MILLIS.milliseconds)
        repeat(greeting.length) { index ->
            greetingCharacterCount = index + 1
            delay(GREETING_CHARACTER_DELAY_MILLIS.milliseconds)
        }
        typingStage = TypingStage.GREETING_PAUSE
        delay(GREETING_CURSOR_HOLD_MILLIS.milliseconds)
        typingStage = TypingStage.TITLE
        repeat(titlePrefix.length + titleEmphasis.length) { index ->
            titleCharacterCount = index + 1
            delay(TITLE_CHARACTER_DELAY_MILLIS.milliseconds)
        }
        typingStage = TypingStage.TITLE_PAUSE
        delay(CONTENT_HOLD_DURATION_MILLIS.milliseconds)
        currentOnFinished()
    }

    IntermediateSplashContent(
        greeting = greeting.take(greetingCharacterCount),
        titlePrefix = titlePrefix.take(titleCharacterCount),
        titleEmphasis = titleEmphasis.take((titleCharacterCount - titlePrefix.length).coerceAtLeast(0)),
        typingStage = typingStage,
        modifier = modifier,
    )
}

/**
 * Figma의 중간 스플래시 레이아웃과 색상 강조를 그린다.
 *
 * @param greeting 현재까지 입력된 보조 문구
 * @param titlePrefix 현재까지 입력된 핵심 문구의 기본 색상 부분
 * @param titleEmphasis 현재까지 입력된 핵심 문구의 강조 색상 부분
 * @param typingStage 현재 입력하거나 커서를 깜빡이는 문구
 * @param modifier 화면 전체의 크기와 배치를 지정할 수식자
 */
@Composable
private fun IntermediateSplashContent(
    greeting: String,
    titlePrefix: String,
    titleEmphasis: String,
    typingStage: TypingStage,
    modifier: Modifier = Modifier,
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = greeting,
                        color = GitItTheme.colors.grey400,
                        style = GitItTheme.typography.splashSubtitle,
                        maxLines = 1,
                    )
                    TypingCursor(
                        color = GitItTheme.colors.grey400,
                        width = 2.dp,
                        height = 22.dp,
                        visible = typingStage == TypingStage.GREETING || typingStage == TypingStage.GREETING_PAUSE,
                        blinking = typingStage == TypingStage.GREETING_PAUSE,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        style = GitItTheme.typography.splashTitle,
                        maxLines = 1,
                    )
                    TypingCursor(
                        color = GitItTheme.colors.blue200,
                        width = 3.dp,
                        height = 40.dp,
                        visible = typingStage == TypingStage.TITLE || typingStage == TypingStage.TITLE_PAUSE,
                        blinking = typingStage == TypingStage.TITLE_PAUSE,
                    )
                }
            }
        }
    }
}

/**
 * 입력 위치에 고정 표시되며 입력을 마치면 점멸하는 커서를 그린다.
 *
 * @param color 현재 입력 중인 문구와 맞출 커서 색상
 * @param width 커서 막대의 너비
 * @param height 커서 막대의 높이
 * @param visible 현재 입력 줄에 커서를 노출할지 여부
 * @param blinking 입력을 마친 커서가 점멸해야 하는지 여부
 */
@Composable
private fun TypingCursor(
    color: Color,
    width: Dp,
    height: Dp,
    visible: Boolean,
    blinking: Boolean,
) {
    val blinkAlpha =
        key(blinking) {
            val transition = rememberInfiniteTransition(label = "intermediateSplashCursor")
            val alpha by
                transition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec =
                        infiniteRepeatable(
                            animation =
                                keyframes {
                                    durationMillis = CURSOR_BLINK_DURATION_MILLIS
                                    1f at CURSOR_BLINK_DURATION_MILLIS / 2 - 1
                                    0f at CURSOR_BLINK_DURATION_MILLIS / 2
                                },
                        ),
                    label = "intermediateSplashCursorAlpha",
                )
            alpha
        }

    Box(
        modifier =
            Modifier
                .padding(start = 4.dp)
                .width(width)
                .height(height)
                .alpha(
                    if (visible) {
                        if (blinking) {
                            blinkAlpha
                        } else {
                            1f
                        }
                    } else {
                        0f
                    },
                ).background(color),
    )
}

@Preview
@Composable
private fun IntermediateSplashContentPreview() {
    GitItTheme {
        IntermediateSplashContent(
            greeting = "Hello World",
            titlePrefix = "Let’s ",
            titleEmphasis = "Git-it!",
            typingStage = TypingStage.TITLE_PAUSE,
        )
    }
}

/** 중간 화면이 보이는 동안 플랫폼 시스템 바를 어두운 배경에 맞게 조정한다. */
@Composable
internal expect fun IntermediateSplashSystemBarsEffect()
