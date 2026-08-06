package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme

@Preview(name = "Liquid Glass Nav Bar - White")
@Composable
private fun GitItLiquidGlassNavBarWhitePreview() {
    GitItLiquidGlassNavBarPreviewScreen(backgroundColor = GitItTheme.colors.grey100)
}

@Preview(name = "Liquid Glass Nav Bar - Black")
@Composable
private fun GitItLiquidGlassNavBarBlackPreview() {
    GitItLiquidGlassNavBarPreviewScreen(backgroundColor = GitItTheme.colors.grey700)
}

/**
 * 리퀴드 글래스 네비게이션 바를 상호작용 가능한 상태로 프리뷰에 배치한다.
 *
 * @param backgroundColor 네비게이션 바 대비를 확인할 프리뷰 배경색
 */
@Composable
private fun GitItLiquidGlassNavBarPreviewScreen(backgroundColor: Color) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val items =
        listOf(
            GitItLiquidGlassNavBarItem(label = "홈", icon = { NavPreviewHomeIcon() }),
            GitItLiquidGlassNavBarItem(label = "프로젝트", icon = { NavPreviewFileIcon() }),
            GitItLiquidGlassNavBarItem(label = "저장", icon = { NavPreviewBookmarkIcon() }),
            GitItLiquidGlassNavBarItem(label = "마이", icon = { NavPreviewUserIcon() }),
        )
    GitItTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GitItLiquidGlassNavBar(
                items = items,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = { selectedIndex = it },
            )
        }
    }
}

@Composable
private fun NavPreviewHomeIcon() =
    NavPreviewIcon { size, color, stroke ->
        val w = size.width
        val h = size.height
        val path =
            Path().apply {
                moveTo(w * 0.5f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.45f)
                lineTo(w * 0.85f, h * 0.85f)
                lineTo(w * 0.58f, h * 0.85f)
                lineTo(w * 0.58f, h * 0.6f)
                lineTo(w * 0.42f, h * 0.6f)
                lineTo(w * 0.42f, h * 0.85f)
                lineTo(w * 0.15f, h * 0.85f)
                lineTo(w * 0.15f, h * 0.45f)
                close()
            }
        drawPath(path, color, style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

@Composable
private fun NavPreviewFileIcon() =
    NavPreviewIcon { size, color, stroke ->
        val w = size.width
        val h = size.height
        val path =
            Path().apply {
                moveTo(w * 0.2f, h * 0.1f)
                lineTo(w * 0.65f, h * 0.1f)
                lineTo(w * 0.8f, h * 0.3f)
                lineTo(w * 0.8f, h * 0.9f)
                lineTo(w * 0.2f, h * 0.9f)
                close()
            }
        drawPath(path, color, style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color, Offset(w * 0.35f, h * 0.5f), Offset(w * 0.65f, h * 0.5f), strokeWidth = stroke.toPx(), cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.35f, h * 0.65f), Offset(w * 0.65f, h * 0.65f), strokeWidth = stroke.toPx(), cap = StrokeCap.Round)
    }

@Composable
private fun NavPreviewBookmarkIcon() =
    NavPreviewIcon { size, color, stroke ->
        val w = size.width
        val h = size.height
        val path =
            Path().apply {
                moveTo(w * 0.25f, h * 0.1f)
                lineTo(w * 0.75f, h * 0.1f)
                lineTo(w * 0.75f, h * 0.9f)
                lineTo(w * 0.5f, h * 0.65f)
                lineTo(w * 0.25f, h * 0.9f)
                close()
            }
        drawPath(path, color, style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

@Composable
private fun NavPreviewUserIcon() =
    NavPreviewIcon { size, color, stroke ->
        val w = size.width
        val h = size.height
        drawCircle(color, radius = h * 0.2f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(width = stroke.toPx()))
        val bodyPath =
            Path().apply {
                moveTo(w * 0.15f, h * 0.9f)
                cubicTo(w * 0.15f, h * 0.6f, w * 0.85f, h * 0.6f, w * 0.85f, h * 0.9f)
            }
        drawPath(bodyPath, color, style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round))
    }

private typealias NavIconDrawer =
    DrawScope.(
        size: Size,
        color: Color,
        stroke: Dp,
    ) -> Unit

@Composable
private fun NavPreviewIcon(draw: NavIconDrawer) {
    val color = LocalContentColor.current
    val strokeWidth = 2.dp
    Canvas(modifier = Modifier.size(24.dp)) {
        draw(size, color, strokeWidth)
    }
}
