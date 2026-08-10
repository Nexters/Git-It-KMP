package com.nexters.hytime.gitit.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination

/**
 * 홈 기능이 준비되기 전 하단 탭바만 표시하는 화면이다.
 *
 * @param uiState 화면에 표시할 홈 UI 상태
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param modifier 홈 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        GitItMainNavBar(
            selectedDestination = GitItMainNavDestination.Home,
            onDestinationClick = { destination ->
                when (destination) {
                    GitItMainNavDestination.Home -> onIntent(HomeIntent.HomeTabClick)
                    GitItMainNavDestination.Project -> onIntent(HomeIntent.ProjectTabClick)
                    GitItMainNavDestination.Saved -> onIntent(HomeIntent.SavedTabClick)
                    GitItMainNavDestination.My -> onIntent(HomeIntent.MyTabClick)
                }
            },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 27.dp, end = 27.dp, bottom = 29.dp),
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    GitItTheme {
        HomeScreen(uiState = HomeUiState(), onIntent = {})
    }
}
