package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.skydoves.cloudy.CloudyProgressive
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.cloudy

/**
 * 상단은 선명하게 흐리고 아래로 갈수록 투명해지는 배경을 적용한다.
 *
 * @param sky 뒤쪽 콘텐츠를 캡처한 Cloudy 상태. null이면 그라데이션 dim만 표시한다
 * @return 점진 블러와 상단 dim이 적용된 수식자
 */
@Composable
fun Modifier.gitItTopGradientBlur(sky: Sky?): Modifier {
    val gradient =
        Brush.verticalGradient(
            colorStops =
                arrayOf(
                    0f to GitItTheme.colors.grey700.copy(alpha = TOP_DIM_ALPHA),
                    TOP_BLUR_FADE_START to GitItTheme.colors.grey700.copy(alpha = TOP_DIM_ALPHA),
                    1f to Color.Transparent,
                ),
        )
    val blurred =
        if (sky == null) {
            this
        } else {
            cloudy(
                sky = sky,
                radius = TOP_BLUR_RADIUS,
                progressive =
                    CloudyProgressive.TopToBottom(
                        start = TOP_BLUR_FADE_START,
                        end = 1f,
                    ),
                tint = Color.Transparent,
                shape = RectangleShape,
            )
        }

    return blurred.background(gradient)
}

/** 상단 배경에 적용할 Cloudy 블러 반경(px). */
private const val TOP_BLUR_RADIUS = 20

/** 블러와 dim이 아래쪽으로 흐려지기 시작하는 정규화 위치. */
private const val TOP_BLUR_FADE_START = 0.75f

/** 상단 dim의 최대 불투명도. */
private const val TOP_DIM_ALPHA = 0.6f
