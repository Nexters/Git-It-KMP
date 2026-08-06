package com.nexters.hytime.gitit.presentation.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.icons.GitItBackIcon
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonState
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant
import com.nexters.hytime.gitit.designsystem.liquidglass.gitItLiquidGlassBackdrop
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky

@Composable
fun LiquidGlassBackButtonExampleScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = rememberSky()

    val buttonStart = 20.dp
    val buttonTop = 56.dp
    val buttonSize = GitItLiquidGlassIconButtonSize.Md
    val glassShape = RoundedCornerShape(99.dp)
    var backdropSize by remember { mutableStateOf(IntSize.Zero) }

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

        Box(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(start = buttonStart, top = buttonTop),
        ) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .onSizeChanged { backdropSize = it }
                        .gitItLiquidGlassBackdrop(
                            sky = sky,
                            backdropSize = backdropSize,
                            shape = glassShape,
                        ),
            )

            GitItLiquidGlassIconButton(
                onClick = onBackClick,
                size = buttonSize,
                variant = GitItLiquidGlassIconButtonVariant.Secondary,
                state = GitItLiquidGlassIconButtonState.Default,
            ) {
                GitItBackIcon(size = buttonSize)
            }
        }
    }
}

@Composable
private fun LiquidGlassExampleTextList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
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
