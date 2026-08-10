package com.nexters.hytime.gitit.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 홈 기능의 상태와 이벤트를 화면에 연결하는 진입점이다.
 *
 * 현재는 하단 탭바만 표시한다.
 *
 * @param onNavigateToProjectList 프로젝트 리스트 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 * @param onNavigateToMy 마이 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 * @param onNavigateToBookmark 저장한 문제 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 */
@Composable
fun HomeRoute(
    onNavigateToProjectList: () -> Unit = {},
    onNavigateToMy: () -> Unit = {},
    onNavigateToBookmark: () -> Unit = {},
) {
    val viewModel = viewModel { HomeViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                HomeSideEffect.NavigateToProjectList -> onNavigateToProjectList()
                HomeSideEffect.NavigateToMy -> onNavigateToMy()
                HomeSideEffect.NavigateToBookmark -> onNavigateToBookmark()
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
