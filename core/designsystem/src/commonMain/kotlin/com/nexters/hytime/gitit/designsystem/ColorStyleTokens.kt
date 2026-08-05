package com.nexters.hytime.gitit.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Git-it의 컬러 스타일 토큰을 제공한다.
 *
 * @property gradient1 퍼플에서 슬레이트 블루로 이어지는 어두운 선형 그라디언트
 * @property gradient2 블랙에서 라이트 블루로 이어지는 대비가 큰 선형 그라디언트
 * @property gradient3 블루에서 페일 블루로 이어지는 밝은 선형 그라디언트
 */
@Immutable
class GitItColorStyles internal constructor(
    val gradient1: Brush,
    val gradient2: Brush,
    val gradient3: Brush,
)

/** Figma 컬러 스타일과 일치하는 기본 그라디언트 토큰이다. */
internal val defaultGitItColorStyles =
    GitItColorStyles(
        gradient1 = figmaLinearGradient(Color(0xFF3B3749), Color(0xFF56718A)),
        gradient2 = figmaLinearGradient(Color(0xFF141414), Color(0xFFA5C4F0)),
        gradient3 = figmaLinearGradient(Color(0xFF82ACE5), Color(0xFFD5E7FE)),
    )

/**
 * Figma 컬러 스타일의 각도와 색상을 보존하는 선형 그라디언트를 만든다.
 *
 * @param start 시작 지점의 색상
 * @param end 끝 지점의 색상
 * @return 컴포넌트 크기에 맞춰 그려지는 선형 그라디언트
 */
private fun figmaLinearGradient(
    start: Color,
    end: Color,
): Brush = AngledLinearGradientBrush(colors = listOf(start, end))

/**
 * CSS 방식의 각도를 Compose 좌표계로 변환해 그라디언트 셰이더를 만든다.
 *
 * @property colors 시작점부터 끝점까지 적용할 색상 목록
 */
private class AngledLinearGradientBrush(
    private val colors: List<Color>,
) : ShaderBrush() {
    /**
     * 주어진 그리기 영역 전체를 덮는 선형 그라디언트 셰이더를 생성한다.
     *
     * @param size 그라디언트를 그릴 영역의 크기
     * @return Figma 각도를 반영한 선형 그라디언트 셰이더
     */
    override fun createShader(size: Size): Shader {
        val radians = FIGMA_GRADIENT_ANGLE_DEGREES * PI.toFloat() / 180f
        val direction = Offset(sin(radians), -cos(radians))
        val halfLength = (abs(direction.x) * size.width + abs(direction.y) * size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val delta = Offset(direction.x * halfLength, direction.y * halfLength)

        return LinearGradientShader(
            from = center - delta,
            to = center + delta,
            colors = colors,
        )
    }
}

/** Figma가 내보낸 컬러 스타일의 CSS 선형 그라디언트 각도다. */
private const val FIGMA_GRADIENT_ANGLE_DEGREES = 191.73166f

/**
 * 컬러 스타일의 이름과 그라디언트 샘플을 프리뷰에 표시한다.
 *
 * @param name 프리뷰에 표시할 컬러 스타일 이름
 * @param brush 확인할 그라디언트 브러시
 * @param modifier 샘플 영역의 크기와 배치를 지정할 수식자
 */
@Composable
private fun ColorStylePreviewItem(
    name: String,
    brush: Brush,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.caption1,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush),
        )
    }
}

@Preview
@Composable
private fun DefaultGitItColorStylesPreview() {
    GitItTheme {
        Column(
            modifier =
                Modifier
                    .width(320.dp)
                    .background(GitItTheme.colors.grey700)
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ColorStylePreviewItem(
                name = "Gradient 1",
                brush = defaultGitItColorStyles.gradient1,
            )
            ColorStylePreviewItem(
                name = "Gradient 2",
                brush = defaultGitItColorStyles.gradient2,
            )
            ColorStylePreviewItem(
                name = "Gradient 3",
                brush = defaultGitItColorStyles.gradient3,
            )
        }
    }
}
