package com.nexters.hytime.gitit.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * 온보딩 기능의 상태와 이벤트를 화면에 연결하는 진입점이다.
 *
 * [OnboardingViewModel]을 주입해 로그인 상태를 수집하고, 성공 시
 * [onSignInSuccess]를 호출한다. 화면 자체는 상태를 소유하지 않는
 * [OnboardingScreen]에 상태와 이벤트를 넘긴다.
 *
 * @param onSignInSuccess 로그인 성공 시 호출될 콜백
 */
@Composable
fun OnboardingRoute(onSignInSuccess: () -> Unit) {
    val viewModel = koinViewModel<OnboardingViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is OnboardingUiState.Success) {
            onSignInSuccess()
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onGoogleLoginClick = viewModel::signIn,
    )
}
