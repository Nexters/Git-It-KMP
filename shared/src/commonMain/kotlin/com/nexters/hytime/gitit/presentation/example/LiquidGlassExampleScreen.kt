package com.nexters.hytime.gitit.presentation.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.icons.GitItBackIcon
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassButtonGroup
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassNavBar
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassNavBarItem
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky

@Composable
fun LiquidGlassExampleScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = rememberSky()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Black),
    ) {
        LiquidGlassExampleTextList(
            modifier =
                Modifier
                    .fillMaxSize()
                    .sky(sky),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 56.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            GitItTopBar(
                type = GitItTopBarType.Default,
                sky = sky,
                onBackClick = onBackClick,
                actions = { LiquidGlassExampleActions(sky = sky) },
            )
            GitItTopBar(
                type = GitItTopBarType.LargeTitle,
                sky = sky,
                title = "저장소",
                subtitle = "12개의 저장소",
                onBackClick = onBackClick,
                actions = { LiquidGlassExampleActions(sky = sky) },
            )
            GitItTopBar(
                type = GitItTopBarType.InlineUser,
                sky = sky,
                userName = "김이박",
                userSubtitle = "Junior Developer",
                userAvatar = {
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .background(GitItTheme.colors.grey500),
                    )
                },
                actions = { LiquidGlassExampleActions(sky = sky) },
            )
            GitItTopBar(
                type = GitItTopBarType.InlineTitle,
                sky = sky,
                title = "저장소",
                subtitle = "12개의 저장소",
                actions = { LiquidGlassExampleActions(sky = sky) },
            )
        }

        LiquidGlassExampleNavBar(
            sky = sky,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
        )
    }
}

@Composable
private fun LiquidGlassExampleActions(sky: Sky) {
    GitItLiquidGlassButtonGroup(sky = sky) {
        GitItLiquidGlassIconButton(
            onClick = {},
            size = GitItLiquidGlassIconButtonSize.Sm,
            variant = GitItLiquidGlassIconButtonVariant.Text,
        ) {
            GitItBackIcon(size = GitItLiquidGlassIconButtonSize.Sm)
        }
        GitItLiquidGlassIconButton(
            onClick = {},
            size = GitItLiquidGlassIconButtonSize.Sm,
            variant = GitItLiquidGlassIconButtonVariant.Text,
        ) {
            GitItBackIcon(size = GitItLiquidGlassIconButtonSize.Sm)
        }
    }
}

@Composable
private fun LiquidGlassExampleNavBar(
    sky: Sky,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val items =
        listOf(
            GitItLiquidGlassNavBarItem(label = "홈", icon = { LiquidGlassExampleNavIcon("H") }),
            GitItLiquidGlassNavBarItem(label = "프로젝트", icon = { LiquidGlassExampleNavIcon("P") }),
            GitItLiquidGlassNavBarItem(label = "저장", icon = { LiquidGlassExampleNavIcon("S") }),
            GitItLiquidGlassNavBarItem(label = "마이", icon = { LiquidGlassExampleNavIcon("M") }),
        )

    GitItLiquidGlassNavBar(
        items = items,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it },
        modifier = modifier,
        sky = sky,
    )
}

@Composable
private fun LiquidGlassExampleNavIcon(label: String) {
    Text(
        text = label,
        color = GitItTheme.colors.blue100,
        style = GitItTheme.typography.caption2,
    )
}

@Composable
private fun LiquidGlassExampleTextList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 140.dp),
    ) {
        items(LIQUID_GLASS_EXAMPLE_PARAGRAPHS) { paragraph ->
            Text(
                text = paragraph,
                modifier = Modifier.padding(bottom = 18.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

private val LIQUID_GLASS_EXAMPLE_PARAGRAPHS =
    List(48) { index ->
        "Lorem ipsum ${index + 1}. Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Integer vitae ligula sed lorem volutpat facilisis. Donec non arcu at ipsum " +
            "vehicula tincidunt. Praesent posuere, massa id luctus cursus, justo lectus " +
            "tempus nunc, vitae tincidunt sem erat ac nibh."
    }
