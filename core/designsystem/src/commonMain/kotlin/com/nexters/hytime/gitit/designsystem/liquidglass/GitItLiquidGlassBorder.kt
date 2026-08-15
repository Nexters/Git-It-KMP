package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** 리퀴드 글래스 테두리 하이라이트 방향이다. */
internal enum class GitItLiquidGlassBorderKind {
    /** 상단과 하단이 강조되고 좌우가 흐려지는 형태다. */
    TopBottom,

    /** 좌상단과 우하단이 강조되고 우상단과 좌하단이 흐려지는 형태다. */
    Diagonal,

    /** 가로 pill 네비게이션 바 전용 형태다. [Diagonal]과 별개 각도로 튜닝한다. */
    NavBar,
}

/**
 * FoodDiary Glassmorphism처럼 투명 stop이 포함된 전체 stroke를 그린다.
 *
 * [color]의 알파는 무시되고 [startAlpha]/[endAlpha]로 그라디언트 양 끝 밝기를 각각 결정한다.
 * 양 끝 모두 `0f`면 보더가 전혀 보이지 않는다. 기본값은 원본 그라디언트(시작 0.62 / 끝 0.28)로
 * 정중앙(0.5)에서 완전 투명해진다.
 *
 * @param borderKind 테두리 하이라이트가 흐르는 방향
 * @param color 보더 색상. 알파는 사용되지 않는다
 * @param startAlpha 시작점(밝은 모서리) 알파(0f..1f)
 * @param endAlpha 끝점(흐린 모서리) 알파(0f..1f)
 * @param cornerRadius 테두리 모서리 반지름. null이면 높이의 절반을 사용한다
 */
internal fun DrawScope.drawGitItLiquidGlassBorder(
    borderKind: GitItLiquidGlassBorderKind,
    color: Color = Color.White,
    startAlpha: Float = GIT_IT_LIQUID_GLASS_BORDER_START_ALPHA,
    endAlpha: Float = GIT_IT_LIQUID_GLASS_BORDER_END_ALPHA,
    cornerRadius: Dp? = null,
) {
    require(startAlpha in 0f..1f) { "startAlpha는 0f..1f 범위여야 한다: $startAlpha" }
    require(endAlpha in 0f..1f) { "endAlpha는 0f..1f 범위여야 한다: $endAlpha" }
    val strokeWidth = 2.2.dp.toPx()
    val angleDegrees =
        when (borderKind) {
            GitItLiquidGlassBorderKind.TopBottom -> GIT_IT_LIQUID_GLASS_TOP_BOTTOM_BORDER_ANGLE_DEGREES
            GitItLiquidGlassBorderKind.Diagonal -> GIT_IT_LIQUID_GLASS_DIAGONAL_BORDER_ANGLE_DEGREES
            GitItLiquidGlassBorderKind.NavBar -> GIT_IT_LIQUID_GLASS_NAV_BAR_BORDER_ANGLE_DEGREES
        }
    val (start, end) = gradientOffsetsForAngle(size, angleDegrees)
    val borderBrush =
        Brush.linearGradient(
            colorStops =
                arrayOf(
                    0f to color.copy(alpha = startAlpha),
                    0.5f to color.copy(alpha = 0f),
                    1f to color.copy(alpha = endAlpha),
                ),
            start = start,
            end = end,
        )

    drawRoundRect(
        brush = borderBrush,
        cornerRadius = CornerRadius(cornerRadius?.toPx() ?: (size.height / 2f)),
        style = Stroke(width = strokeWidth),
    )
}

/** 지정한 각도를 선형 그라디언트가 컴포넌트 전체를 덮는 시작점과 끝점으로 변환한다. */
private fun gradientOffsetsForAngle(
    size: Size,
    angleDegrees: Float,
): Pair<Offset, Offset> {
    val radians = Math.toRadians(angleDegrees.toDouble())
    val directionX = sin(radians).toFloat()
    val directionY = -cos(radians).toFloat()
    val halfProjection = abs(directionX) * size.width / 2f + abs(directionY) * size.height / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    return Offset(
        x = center.x - directionX * halfProjection,
        y = center.y - directionY * halfProjection,
    ) to
        Offset(
            x = center.x + directionX * halfProjection,
            y = center.y + directionY * halfProjection,
        )
}

private const val GIT_IT_LIQUID_GLASS_TOP_BOTTOM_BORDER_ANGLE_DEGREES = 180f
private const val GIT_IT_LIQUID_GLASS_DIAGONAL_BORDER_ANGLE_DEGREES = 135f

/** 가로 pill 네비게이션 바 보더 각도 */
private const val GIT_IT_LIQUID_GLASS_NAV_BAR_BORDER_ANGLE_DEGREES = 160f

/** 보더 시작점(밝은 모서리) 알파. */
private const val GIT_IT_LIQUID_GLASS_BORDER_START_ALPHA = 0.62f

/** 보더 끝점(흐린 모서리) 알파. */
private const val GIT_IT_LIQUID_GLASS_BORDER_END_ALPHA = 0.28f
