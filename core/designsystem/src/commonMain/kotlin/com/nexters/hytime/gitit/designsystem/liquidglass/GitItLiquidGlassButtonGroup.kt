package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.skydoves.cloudy.Sky

/**
 * 여러 아이콘 버튼을 하나의 글래스 필(pill) 컨테이너로 묶는다.
 *
 * Figma의 "Button - Liquid Glass - Group" 컴포넌트와 대응한다.
 * 내부 아이콘 버튼의 글래스 모피즘 테두리와 동일한 그라디언트 stroke를 컨테이너 테두리에 적용한다.
 * [sky]를 전달하면 리퀴드 글래스 백드롭 효과가 추가로 적용된다.
 * [content]에는 보통 [GitItLiquidGlassIconButton][com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton]
 * (size = Sm, variant = Text)을 개수만큼 배치한다.
 *
 * @param modifier 컨테이너의 외부 배치와 추가 수식자
 * @param sky 흐림 배경을 캡처하는 Cloudy 상태. null이면 백드롭 없이 정적 배경만 그린다
 * @param content 필 컨테이너 안에 배치할 아이콘 버튼 콘텐츠
 */
@Composable
fun GitItLiquidGlassButtonGroup(
    modifier: Modifier = Modifier,
    sky: Sky? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val groupContent: @Composable () -> Unit = {
        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(GitItTheme.colors.white15)
                    .drawWithContent {
                        drawContent()
                        drawGitItLiquidGlassBorder(GitItLiquidGlassBorderKind.TopBottom)
                    }.padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }

    if (sky != null) {
        GitItLiquidGlassContainer(sky = sky, modifier = modifier) { groupContent() }
    } else {
        Box(modifier = modifier) { groupContent() }
    }
}
