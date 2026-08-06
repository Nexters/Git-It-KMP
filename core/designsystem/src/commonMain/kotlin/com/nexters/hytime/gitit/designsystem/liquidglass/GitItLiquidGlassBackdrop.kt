package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.skydoves.cloudy.LiquidGlassDefaults
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.cloudy
import com.skydoves.cloudy.liquidGlass

/**
 * Git-It 백 버튼 뒤에 쓰는 리퀴드 글래스 배경을 그린다.
 *
 * @param sky 흐림 배경을 캡처하는 Cloudy 상태
 * @param backdropSize 레이아웃 이후 측정된 배경 크기. 0이면 리퀴드 글래스 효과를 건너뛴다
 * @param shape 배경과 흐림 효과를 자를 형태
 */
@Composable
fun Modifier.gitItLiquidGlassBackdrop(
    sky: Sky,
    backdropSize: IntSize,
    shape: Shape = RoundedCornerShape(99.dp),
): Modifier {
    val lensWidth = backdropSize.width.toFloat()
    val lensHeight = backdropSize.height.toFloat()
    val clippedModifier = clip(shape)

    if (lensWidth <= 0f || lensHeight <= 0f) {
        return clippedModifier.cloudy(
            sky = sky,
            radius = 0,
            tint = GitItTheme.colors.white15,
            shape = shape,
        )
    }

    return clippedModifier
        .liquidGlass(
            lensCenter = Offset(lensWidth / 2f, lensHeight / 2f),
            lensSize = Size(lensWidth, lensHeight),
            refraction = 0.5f,
            glow = LiquidGlassDefaults.NoGlow,
            enabled = true,
        ).cloudy(
            sky = sky,
            radius = 0,
            tint = GitItTheme.colors.white15,
            shape = shape,
        )
}
