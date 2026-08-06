package com.nexters.hytime.gitit.designsystem.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.icons.GitItBackIcon
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant

@Preview(name = "Top Bar - Default")
@Composable
private fun GitItTopBarDefaultPreview() {
    GitItTopBarPreviewContainer {
        GitItTopBar(
            type = GitItTopBarType.Default,
            onBackClick = {},
            actions = { GitItTopBarSampleActions() },
        )
    }
}

@Preview(name = "Top Bar - Large Title")
@Composable
private fun GitItTopBarLargeTitlePreview() {
    GitItTopBarPreviewContainer {
        GitItTopBar(
            type = GitItTopBarType.LargeTitle,
            title = "저장소",
            subtitle = "12개의 저장소",
            onBackClick = {},
            actions = { GitItTopBarSampleActions() },
        )
    }
}

@Preview(name = "Top Bar - Inline User")
@Composable
private fun GitItTopBarInlineUserPreview() {
    GitItTopBarPreviewContainer {
        GitItTopBar(
            type = GitItTopBarType.InlineUser,
            userName = "김이박",
            userSubtitle = "Junior Developer",
            userAvatar = { GitItTopBarSampleAvatar() },
            actions = { GitItTopBarSampleActions() },
        )
    }
}

@Preview(name = "Top Bar - Inline Title")
@Composable
private fun GitItTopBarInlineTitlePreview() {
    GitItTopBarPreviewContainer {
        GitItTopBar(
            type = GitItTopBarType.InlineTitle,
            title = "저장소",
            subtitle = "12개의 저장소",
            actions = { GitItTopBarSampleActions() },
        )
    }
}

@Composable
private fun GitItTopBarPreviewContainer(content: @Composable () -> Unit) {
    GitItTheme {
        Column(
            modifier =
                Modifier
                    .width(360.dp)
                    .background(GitItTheme.colors.grey700)
                    .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun GitItTopBarSampleAvatar() {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .background(GitItTheme.colors.grey500),
    )
}

@Composable
private fun GitItTopBarSampleActions() {
    GitItLiquidGlassButtonGroup {
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
