package com.nexters.hytime.gitit.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.hytime.gitit.designsystem.GitItTheme

/**
 * 앱 시작 시 토큰 검증이 끝날 때까지 표시하는 스플래시 화면이다.
 *
 * @param uiState 토큰 검증 진행 상태. 후속 디자인에서 화면 표현에 사용한다
 * @param modifier 화면 크기와 배치를 지정할 수식자
 */
@Composable
fun SplashScreen(
    uiState: SplashUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    )
}

@Preview
@Composable
private fun SplashScreenPreview() {
    GitItTheme {
        SplashScreen(uiState = SplashUiState())
    }
}
