package com.nexters.hytime.gitit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.navigation.AppNavHost
import com.nexters.hytime.gitit.presentation.app.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * 앱 공통 테마와 최상위 내비게이션을 제공한다.
 */
@Composable
fun App() {
    val viewModel = koinViewModel<AppViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppContent(isSignedIn = uiState.isSignedIn)
}

/**
 * 앱 테마와 인증 상태에 맞는 최상위 내비게이션을 렌더링한다.
 *
 * @param isSignedIn 저장된 로그인 세션이 있는지 여부
 */
@Composable
private fun AppContent(isSignedIn: Boolean) {
    GitItTheme {
        AppNavHost(isSignedIn = isSignedIn)
    }
}

@Preview
@Composable
fun AppPreview() = AppContent(isSignedIn = false)
