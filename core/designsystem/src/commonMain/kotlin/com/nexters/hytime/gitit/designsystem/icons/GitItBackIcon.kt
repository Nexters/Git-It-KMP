package com.nexters.hytime.gitit.designsystem.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize

/**
 * 뒤로가기 용도의 chevron left 아이콘을 그린다.
 *
 * @param size 아이콘을 올릴 리퀴드 글래스 버튼 크기
 * @param modifier 아이콘의 외부 배치와 추가 수식자
 */
@Composable
fun GitItBackIcon(
    size: GitItLiquidGlassIconButtonSize,
    modifier: Modifier = Modifier,
) {
    val color = LocalContentColor.current
    val iconSize = if (size == GitItLiquidGlassIconButtonSize.Md) 24.dp else 20.dp
    val strokeWidth = if (size == GitItLiquidGlassIconButtonSize.Md) 2.5.dp else 2.dp

    Canvas(modifier = modifier.size(iconSize)) {
        val centerX = this.size.width / 2f
        val centerY = this.size.height / 2f
        val halfWidth = this.size.width * 0.125f
        val halfHeight = this.size.height * 0.25f
        val path =
            Path().apply {
                moveTo(centerX + halfWidth, centerY - halfHeight)
                lineTo(centerX - halfWidth, centerY)
                lineTo(centerX + halfWidth, centerY + halfHeight)
            }

        drawPath(
            path = path,
            color = color,
            style =
                Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
        )
    }
}
