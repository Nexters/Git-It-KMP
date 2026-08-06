package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.icons.GitItBackIcon

@Preview(name = "Liquid Glass Icon Button - White")
@Composable
private fun GitItLiquidGlassIconButtonWhitePreview() {
    GitItLiquidGlassIconButtonPreviewGrid(backgroundColor = GitItTheme.colors.grey100)
}

@Preview(name = "Liquid Glass Icon Button - Black")
@Composable
private fun GitItLiquidGlassIconButtonBlackPreview() {
    GitItLiquidGlassIconButtonPreviewGrid(backgroundColor = GitItTheme.colors.grey700)
}

/**
 * 모든 리퀴드 글래스 아이콘 버튼 변형을 실제 컴포넌트로 배치한다.
 *
 * @param backgroundColor 버튼 대비를 확인할 프리뷰 배경색
 */
@Composable
private fun GitItLiquidGlassIconButtonPreviewGrid(backgroundColor: Color) {
    GitItTheme {
        Column(
            modifier =
                Modifier
                    .width(360.dp)
                    .background(backgroundColor)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            GitItLiquidGlassIconButtonPreviewSizeGroup(size = GitItLiquidGlassIconButtonSize.Md)
            GitItLiquidGlassIconButtonPreviewSizeGroup(size = GitItLiquidGlassIconButtonSize.Sm)
        }
    }
}

/**
 * 하나의 버튼 크기에 대해 variant와 state 조합을 행렬로 배치한다.
 *
 * @param size 프리뷰할 버튼 크기
 */
@Composable
private fun GitItLiquidGlassIconButtonPreviewSizeGroup(size: GitItLiquidGlassIconButtonSize) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        GitItLiquidGlassIconButtonVariant.entries.forEach { variant ->
            Row(horizontalArrangement = Arrangement.spacedBy(70.dp)) {
                GitItLiquidGlassIconButtonState.entries.forEach { state ->
                    GitItLiquidGlassIconButton(
                        onClick = {},
                        size = size,
                        variant = variant,
                        state = state,
                    ) {
                        GitItBackIcon(size = size)
                    }
                }
            }
        }
    }
}
