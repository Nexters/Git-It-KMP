package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** 리퀴드 글래스 아이콘 버튼의 크기 토큰이다. */
enum class GitItLiquidGlassIconButtonSize {
    /** 40dp 높이의 기본 버튼이다. */
    Md,

    /** 36dp 높이의 작은 버튼이다. */
    Sm,
}

/** 리퀴드 글래스 아이콘 버튼의 표현 방식이다. */
enum class GitItLiquidGlassIconButtonVariant {
    /** 채워진 주요 버튼이다. */
    Primary,

    /** 옅은 흰색 배경을 가진 보조 버튼이다. */
    Secondary,

    /** 주요 색상 아이콘만 표시하는 버튼이다. */
    PrimaryText,

    /** 배경 없이 아이콘만 표시하는 버튼이다. */
    Text,
}

/** 리퀴드 글래스 아이콘 버튼의 시각 상태다. */
enum class GitItLiquidGlassIconButtonState {
    /** 기본 상호작용 상태다. */
    Default,

    /** 비활성 상태다. */
    Disabled,

    /** 오류를 나타내는 상태다. */
    Error,
}

/**
 * Figma의 Liquid Glass Icon Button 컴포넌트를 Compose로 렌더링한다.
 *
 * @param onClick 활성 상태에서 버튼을 눌렀을 때 실행할 동작
 * @param modifier 버튼의 외부 배치와 추가 수식자
 * @param size Figma의 MD/SM 크기 변형
 * @param variant 버튼의 배경과 강조 방식
 * @param state 버튼의 기본·비활성·오류 상태
 * @param icon 버튼 중앙에 배치할 아이콘 콘텐츠
 */
@Composable
fun GitItLiquidGlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: GitItLiquidGlassIconButtonSize = GitItLiquidGlassIconButtonSize.Md,
    variant: GitItLiquidGlassIconButtonVariant = GitItLiquidGlassIconButtonVariant.Primary,
    state: GitItLiquidGlassIconButtonState = GitItLiquidGlassIconButtonState.Default,
    icon: @Composable () -> Unit,
) {
    val style =
        remember(size, variant, state) {
            GitItLiquidGlassIconButtonStyle.resolve(size = size, variant = variant, state = state)
        }

    Box(
        modifier =
            modifier
                .size(style.containerSize)
                .gitItLiquidGlass(style = style)
                .clickable(
                    enabled = state != GitItLiquidGlassIconButtonState.Disabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides style.contentColor) {
            icon()
        }
    }
}

/**
 * 리퀴드 글래스 아이콘 버튼 변형을 그리기 값으로 변환한 내부 스타일이다.
 *
 * @property containerSize 버튼의 정사각형 컨테이너 크기
 * @property cornerRadius pill 형태를 만드는 둥근 모서리 반지름
 * @property backgroundColor 버튼 표면에 적용할 배경 색상
 * @property contentColor 아이콘 슬롯에 전달할 콘텐츠 색상
 * @property isLiquidGlass Cloudy 리퀴드 글래스 효과 적용 여부
 * @property borderKind 버튼 테두리 하이라이트 방향
 * @property hasBorder 그라디언트 테두리 표시 여부
 */
@Immutable
internal data class GitItLiquidGlassIconButtonStyle(
    val containerSize: Dp,
    val cornerRadius: Dp,
    val backgroundColor: Color,
    val contentColor: Color,
    val isLiquidGlass: Boolean,
    val borderKind: GitItLiquidGlassBorderKind,
    val hasBorder: Boolean,
) {
    internal companion object {
        /**
         * Figma variant 값을 Compose에서 바로 그릴 수 있는 스타일 값으로 변환한다.
         *
         * @param size 버튼 크기 변형
         * @param variant 버튼 표현 방식
         * @param state 버튼 시각 상태
         * @return 변형 조합에 대응하는 내부 스타일
         */
        fun resolve(
            size: GitItLiquidGlassIconButtonSize,
            variant: GitItLiquidGlassIconButtonVariant,
            state: GitItLiquidGlassIconButtonState,
        ): GitItLiquidGlassIconButtonStyle {
            val colors = GitItTheme.colors
            val isDisabled = state == GitItLiquidGlassIconButtonState.Disabled
            val isError = state == GitItLiquidGlassIconButtonState.Error
            val isLiquidGlass = (variant == GitItLiquidGlassIconButtonVariant.Primary) && !isDisabled
            val hasBorder =
                variant == GitItLiquidGlassIconButtonVariant.Primary ||
                    variant == GitItLiquidGlassIconButtonVariant.Secondary
            val borderKind =
                if (isLiquidGlass) {
                    GitItLiquidGlassBorderKind.TopBottom
                } else {
                    GitItLiquidGlassBorderKind.Diagonal
                }
            val backgroundColor =
                when {
                    isLiquidGlass && isError -> colors.error
                    isLiquidGlass -> colors.blue100
                    variant == GitItLiquidGlassIconButtonVariant.Primary || variant == GitItLiquidGlassIconButtonVariant.Secondary -> {
                        colors.white15
                    }
                    else -> Color.Transparent
                }
            val contentColor =
                when {
                    isDisabled && variant == GitItLiquidGlassIconButtonVariant.PrimaryText -> colors.blue400
                    isDisabled -> colors.white30
                    isLiquidGlass && isError -> Color.White
                    isLiquidGlass -> colors.grey700
                    isError -> colors.error
                    variant == GitItLiquidGlassIconButtonVariant.PrimaryText -> colors.blue100
                    variant == GitItLiquidGlassIconButtonVariant.Secondary -> Color.White
                    else -> Color.White
                }

            return GitItLiquidGlassIconButtonStyle(
                containerSize = size.containerSize,
                cornerRadius = 99.dp,
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                isLiquidGlass = isLiquidGlass,
                borderKind = borderKind,
                hasBorder = hasBorder,
            )
        }
    }
}

/** 버튼 크기 변형에 대응하는 정사각형 컨테이너 크기다. */
private val GitItLiquidGlassIconButtonSize.containerSize: Dp
    get() = if (this == GitItLiquidGlassIconButtonSize.Md) 40.dp else 36.dp

/** 리퀴드 글래스 테두리 하이라이트 방향이다. */
internal enum class GitItLiquidGlassBorderKind {
    /** 상단과 하단이 강조되고 좌우가 흐려지는 형태다. */
    TopBottom,

    /** 좌상단과 우하단이 강조되고 우상단과 좌하단이 흐려지는 형태다. */
    Diagonal,
}

/**
 * 리퀴드 글래스 버튼 표면과 그라디언트 테두리를 그리는 수식자를 만든다.
 *
 * @param style 버튼 변형에서 계산한 시각 스타일
 * @return 버튼 표면 효과가 적용된 수식자
 */
@Composable
private fun Modifier.gitItLiquidGlass(style: GitItLiquidGlassIconButtonStyle): Modifier {
    val shape = RoundedCornerShape(style.cornerRadius)

    return clip(shape)
        .background(style.backgroundColor)
        .drawWithContent {
            drawContent()

            if (style.hasBorder) {
                drawGitItLiquidGlassBorder(style.borderKind)
            }
        }
}

/** FoodDiary Glassmorphism처럼 투명 stop이 포함된 전체 stroke를 그린다. */
internal fun DrawScope.drawGitItLiquidGlassBorder(borderKind: GitItLiquidGlassBorderKind) {
    val strokeWidth = 2.2.dp.toPx()
    val angleDegrees =
        when (borderKind) {
            GitItLiquidGlassBorderKind.TopBottom -> GIT_IT_LIQUID_GLASS_TOP_BOTTOM_BORDER_ANGLE_DEGREES
            GitItLiquidGlassBorderKind.Diagonal -> GIT_IT_LIQUID_GLASS_DIAGONAL_BORDER_ANGLE_DEGREES
        }
    val (start, end) = gradientOffsetsForAngle(size, angleDegrees)
    val borderBrush =
        Brush.linearGradient(
            colorStops = GIT_IT_LIQUID_GLASS_BORDER_STOPS,
            start = start,
            end = end,
        )

    drawRoundRect(
        brush = borderBrush,
        cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
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

private val GIT_IT_LIQUID_GLASS_BORDER_STOPS =
    arrayOf(
        0f to Color.White.copy(alpha = 0.62f),
        0.5f to Color.White.copy(alpha = 0f),
        1f to Color.White.copy(alpha = 0.28f),
    )
