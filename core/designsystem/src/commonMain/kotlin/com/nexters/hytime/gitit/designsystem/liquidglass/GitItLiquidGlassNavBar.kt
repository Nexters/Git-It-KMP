package com.nexters.hytime.gitit.designsystem.liquidglass

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.skydoves.cloudy.Sky

/** 리퀴드 글래스 네비게이션 바에 표시할 단일 탭 항목이다. */
class GitItLiquidGlassNavBarItem(
    /** 탭 아래에 표시할 라벨 텍스트. */
    val label: String,
    /** 탭 상단에 표시할 아이콘 콘텐츠. [LocalContentColor]로 색상이 전달된다. */
    val icon: @Composable () -> Unit,
)

/**
 * Figma의 리퀴드 글래스 네비게이션 바(네비바)를 Compose로 렌더링한다.
 *
 * 4개 탭을 pill 형태 글래스 컨테이너에 가로로 배치하며, 선택된 탭 뒤에는
 * [GitItTheme.colors.grey400] 20% 하이라이트가 슬라이드 애니메이션으로 이동한다.
 * 아이콘과 라벨은 모두 [GitItTheme.colors.blue100] 색상으로 표시된다.
 *
 * [sky]를 전달하면 [GitItLiquidGlassContainer]로 흐림(liquid glass) 효과가 추가된다.
 *
 * @param items 탭 항목 목록
 * @param selectedIndex 현재 선택된 탭 인덱스
 * @param onSelectedIndexChange 탭을 선택했을 때 실행할 콜백
 * @param modifier 컨테이너의 외부 배치와 추가 수식자
 * @param sky 흐림 배경을 캡처하는 Cloudy 상태. null이면 백드롭 없이 정적 배경만 그린다
 */
@Composable
fun GitItLiquidGlassNavBar(
    items: List<GitItLiquidGlassNavBarItem>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    sky: Sky? = null,
) {
    val itemLayoutInfos = remember { mutableStateMapOf<Int, NavItemLayoutInfo>() }

    val navBarContent: @Composable () -> Unit = {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(NAV_BAR_HEIGHT)
                    .clip(NAV_BAR_SHAPE)
                    .background(GitItTheme.colors.blue300.copy(alpha = NAV_BAR_BACKGROUND_ALPHA))
                    .drawWithContent {
                        drawContent()
                        drawGitItLiquidGlassBorder(
                            borderKind = GitItLiquidGlassBorderKind.NavBar,
                            color = GitItTheme.colors.blue300,
                            endAlpha = NAV_BAR_BORDER_END_ALPHA,
                        )
                    }.padding(horizontal = NAV_BAR_HORIZONTAL_PADDING),
        ) {
            val selectedInfo = itemLayoutInfos[selectedIndex]
            if (selectedInfo != null) {
                val density = LocalDensity.current
                val animatedX by animateDpAsState(
                    targetValue = with(density) { selectedInfo.offsetPx.toDp() },
                    animationSpec = tween(durationMillis = NAV_BAR_ANIMATION_DURATION, easing = FastOutSlowInEasing),
                    label = "navHighlightX",
                )
                val animatedWidth by animateDpAsState(
                    targetValue = with(density) { selectedInfo.widthPx.toDp() },
                    animationSpec = tween(durationMillis = NAV_BAR_ANIMATION_DURATION, easing = FastOutSlowInEasing),
                    label = "navHighlightWidth",
                )
                Box(
                    modifier =
                        Modifier
                            .offset(x = animatedX)
                            .width(animatedWidth)
                            .fillMaxHeight()
                            .padding(vertical = NAV_BAR_ITEM_VERTICAL_INSET)
                            .clip(NAV_BAR_SHAPE)
                            .background(GitItTheme.colors.grey400.copy(alpha = NAV_BAR_ITEM_HIGHLIGHT_ALPHA)),
                )
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    GitItLiquidGlassNavBarItemView(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectedIndexChange(index) },
                    ) { offsetPx, widthPx ->
                        itemLayoutInfos[index] = NavItemLayoutInfo(offsetPx, widthPx)
                    }
                }
            }
        }
    }

    if (sky != null) {
        GitItLiquidGlassContainer(sky = sky, modifier = modifier.fillMaxWidth(), shape = NAV_BAR_SHAPE) {
            navBarContent()
        }
    } else {
        Box(modifier = modifier.fillMaxWidth()) { navBarContent() }
    }
}

/**
 * 네비게이션 바의 단일 탭을 그린다.
 *
 * 배경은 슬라이드 애니메이션 하이라이트가 담당하므로 이 항목 자체는 투명하며,
 * 클릭 처리와 아이콘·라벨 렌더링만 수행한다.
 *
 * @param item 탭 라벨과 아이콘
 * @param modifier 항목의 외부 배치와 추가 수식자
 * @param onClick 탭 클릭 시 실행할 동작
 * @param onPositioned 레이아웃 이후 이 항목의 x 오프셋(px)과 너비(px)를 전달하는 콜백
 */
@Composable
private fun GitItLiquidGlassNavBarItemView(
    item: GitItLiquidGlassNavBarItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onPositioned: (offsetPx: Float, widthPx: Float) -> Unit,
) {
    Box(
        modifier =
            modifier
                .clip(NAV_BAR_SHAPE)
                .clickable(role = Role.Tab, onClick = onClick)
                .onGloballyPositioned { coords ->
                    onPositioned(
                        coords.positionInParent().x,
                        coords.size.width.toFloat(),
                    )
                }.padding(horizontal = NAV_BAR_ITEM_HORIZONTAL_PADDING, vertical = NAV_BAR_ITEM_VERTICAL_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            CompositionLocalProvider(LocalContentColor provides GitItTheme.colors.blue100) {
                item.icon()
            }
            Text(
                text = item.label,
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.caption2,
            )
        }
    }
}

/** 네비게이션 바 항목의 레이아웃 측정 결과다. */
private data class NavItemLayoutInfo(
    /** 부모(Row) 기준 x 오프셋(px). */
    val offsetPx: Float,
    /** 항목 너비(px). */
    val widthPx: Float,
)

private val NAV_BAR_SHAPE = RoundedCornerShape(99.dp)
private val NAV_BAR_HEIGHT = 64.dp
private val NAV_BAR_HORIZONTAL_PADDING = 7.dp
private val NAV_BAR_ITEM_HORIZONTAL_PADDING = 14.5.dp
private val NAV_BAR_ITEM_VERTICAL_PADDING = 3.dp
private val NAV_BAR_ITEM_VERTICAL_INSET = 7.dp
private const val NAV_BAR_BACKGROUND_ALPHA = 0.1f
private const val NAV_BAR_BORDER_END_ALPHA = 0.6f
private const val NAV_BAR_ITEM_HIGHLIGHT_ALPHA = 0.2f
private const val NAV_BAR_ANIMATION_DURATION = 300
