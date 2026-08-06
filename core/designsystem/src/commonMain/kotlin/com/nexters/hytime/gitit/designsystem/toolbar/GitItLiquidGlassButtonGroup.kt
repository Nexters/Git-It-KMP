package com.nexters.hytime.gitit.designsystem.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassBorderKind
import com.nexters.hytime.gitit.designsystem.liquidglass.drawGitItLiquidGlassBorder

/**
 * 여러 아이콘 버튼을 하나의 글래스 필(pill) 컨테이너로 묶는다.
 *
 * Figma의 "Button - Liquid Glass - Group" 컴포넌트와 대응한다.
 * 내부 아이콘 버튼의 글래스 모피즘 테두리와 동일한 그라디언트 stroke를 컨테이너 테두리에 적용한다.
 * [content]에는 보통 [GitItLiquidGlassIconButton][com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton]
 * (size = Sm, variant = Text)을 개수만큼 배치한다.
 *
 * @param modifier 컨테이너의 외부 배치와 추가 수식자
 * @param content 필 컨테이너 안에 배치할 아이콘 버튼 콘텐츠
 */
@Composable
fun GitItLiquidGlassButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(99.dp))
                .background(GitItTheme.colors.white15)
                .drawWithContent {
                    drawContent()
                    drawGitItLiquidGlassBorder(GitItLiquidGlassBorderKind.Diagonal)
                }.padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}
