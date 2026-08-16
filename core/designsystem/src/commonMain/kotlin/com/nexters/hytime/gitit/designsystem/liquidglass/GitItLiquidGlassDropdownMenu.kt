package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.skydoves.cloudy.Sky

/** Figma 드롭다운 메뉴의 둥근 사각형 형태다. */
private val DROPDOWN_MENU_SHAPE = RoundedCornerShape(12.dp)

/** Figma Glass 2 효과에 대응하는 블러 반경(px)이다. */
private const val DROPDOWN_MENU_BLUR_RADIUS = 20

/**
 * 좌상단·우하단 그라데이션 테두리와 글래스 블러를 적용한 드롭다운 메뉴다.
 *
 * @param modifier 메뉴의 크기와 화면 내 배치를 지정할 수식자
 * @param sky 뒤쪽 콘텐츠를 흐림 배경으로 읽을 Cloudy 상태. null이면 정적 표면만 표시한다
 * @param content 메뉴 안에 세로로 배치할 항목 콘텐츠
 */
@Composable
fun GitItLiquidGlassDropdownMenu(
    modifier: Modifier = Modifier,
    sky: Sky? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val menuContent: @Composable () -> Unit = {
        Column(
            modifier =
                Modifier
                    .clip(DROPDOWN_MENU_SHAPE)
                    .background(GitItTheme.colors.white05)
                    .drawWithContent {
                        drawContent()
                        drawGitItLiquidGlassBorder(
                            borderKind = GitItLiquidGlassBorderKind.Diagonal,
                            cornerRadius = 12.dp,
                        )
                    }.padding(4.dp),
            content = content,
        )
    }

    if (sky == null) {
        Box(modifier = modifier) { menuContent() }
    } else {
        GitItLiquidGlassContainer(
            sky = sky,
            modifier = modifier,
            shape = DROPDOWN_MENU_SHAPE,
            blurRadius = DROPDOWN_MENU_BLUR_RADIUS,
            tint = Color.Transparent,
        ) {
            menuContent()
        }
    }
}

/**
 * 드롭다운 메뉴에서 한 줄 텍스트 동작을 표시한다.
 *
 * @param text 사용자에게 표시할 메뉴 이름
 * @param onClick 항목을 선택했을 때 실행할 동작
 * @param modifier 항목의 크기와 배치를 지정할 수식자
 * @param color 메뉴 이름에 적용할 색상
 */
@Composable
fun GitItLiquidGlassDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = GitItTheme.colors.grey100,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(DROPDOWN_MENU_SHAPE)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 9.5.dp),
    ) {
        Text(text = text, color = color, style = GitItTheme.typography.body2)
    }
}
