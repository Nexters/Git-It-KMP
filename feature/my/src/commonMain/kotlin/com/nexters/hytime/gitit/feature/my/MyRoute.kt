package com.nexters.hytime.gitit.feature.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * 마이 화면의 진입점(Route)이다.
 *
 * @param onNavigateToHome 홈 화면으로 이동하는 콜백
 * @param onNavigateToProjectList 프로젝트 리스트 화면으로 이동하는 콜백
 * @param onNavigateToBookmark 저장한 문제 화면으로 이동하는 콜백
 * @param onNavigateToSettings 설정 화면으로 이동하는 콜백
 */
@Composable
fun MyRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToProjectList: () -> Unit,
    onNavigateToBookmark: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val viewModel = koinViewModel<MyViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                MySideEffect.NavigateToSettings -> onNavigateToSettings()
                MySideEffect.NavigateToHome -> onNavigateToHome()
                MySideEffect.NavigateToProjectList -> onNavigateToProjectList()
                MySideEffect.NavigateToBookmark -> onNavigateToBookmark()
            }
        }
    }

    MyScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
