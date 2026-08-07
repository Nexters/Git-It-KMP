package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
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
private fun Modifier.gitItLiquidGlassBackdrop(
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
            refraction = 0.8f,
            curve = 2f,
            glow = LiquidGlassDefaults.NoGlow,
            enabled = true,
        ).cloudy(
            sky = sky,
            radius = 0,
            tint = GitItTheme.colors.white15,
            shape = shape,
        )
}

/**
 * 콘텐츠 뒤에 리퀴드 글래스 백드롭을 배치하는 컨테이너.
 *
 * 자식 [content]의 크기에 맞춰 [gitItLiquidGlassBackdrop]를 그린 뒤 그 위에 콘텐츠를 올린다.
 * 예제 화면에서 수동으로 Box + matchParentSize + onSizeChanged를 조합하던 패턴을 캡슐화한다.
 *
 * @param sky 흐림 배경을 캡처하는 Cloudy 상태
 * @param modifier 컨테이너의 외부 배치와 추가 수식자
 * @param shape 백드롭 형태
 * @param content 백드롭 위에 배치할 콘텐츠
 */
@Composable
fun GitItLiquidGlassContainer(
    sky: Sky,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(99.dp),
    content: @Composable () -> Unit,
) {
    var backdropSize by remember { mutableStateOf(IntSize.Zero) }
    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .onSizeChanged { backdropSize = it }
                    .gitItLiquidGlassBackdrop(sky = sky, backdropSize = backdropSize, shape = shape),
        )
        content()
    }
}
