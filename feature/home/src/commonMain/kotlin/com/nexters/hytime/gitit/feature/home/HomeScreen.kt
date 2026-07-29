package com.nexters.hytime.gitit.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * 홈 기능이 준비되기 전 표시하는 기본 화면이다.
 *
 * @param uiState 화면에 표시할 홈 UI 상태
 * @param modifier 홈 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(uiState = HomeUiState())
}
