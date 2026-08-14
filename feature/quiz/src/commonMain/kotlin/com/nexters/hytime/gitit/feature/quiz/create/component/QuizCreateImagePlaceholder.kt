package com.nexters.hytime.gitit.feature.quiz.create.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme

/**
 * 디자인 이미지가 확정되기 전까지 중립색 영역을 표시한다.
 * Compose Resources의 painter를 사용하는 [androidx.compose.foundation.Image]로 교체한다.
 *
 * @param modifier 호출 위치에서 placeholder 크기와 배치를 지정하는 수식자
 * @param cornerRadius 표시 영역 모서리의 둥근 정도
 */
@Deprecated(
    message = "Compose Resources 기반 Image로 교체해야 하는 임시 컴포넌트입니다.",
    level = DeprecationLevel.WARNING,
)
@Composable
internal fun QuizCreateImagePlaceholder(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(GitItTheme.colors.grey500),
    )
}
