package com.nexters.hytime.gitit.presentation.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * 앱 시작 스플래시의 상태와 첫 화면 이동을 연결한다.
 *
 * @param onNavigateToHome 유효한 로그인 세션으로 홈에 진입하는 콜백
 * @param onNavigateToOnboarding 로그인해야 하는 사용자를 온보딩으로 보내는 콜백
 */
@Composable
fun SplashRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
) {
    val viewModel = koinViewModel<SplashViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                SplashSideEffect.NavigateToHome -> onNavigateToHome()
                SplashSideEffect.NavigateToOnboarding -> onNavigateToOnboarding()
            }
        }
    }

    SplashScreen(uiState = uiState)
}
