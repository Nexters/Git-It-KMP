package com.nexters.hytime.gitit.feature.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * 저장한 문제 화면의 진입점(Route)이다.
 *
 * @param onNavigateToHome 홈 화면으로 이동하는 콜백
 * @param onNavigateToProjectList 프로젝트 리스트 화면으로 이동하는 콜백
 * @param onNavigateToMy 마이 화면으로 이동하는 콜백
 */
@Composable
fun BookmarkRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToProjectList: () -> Unit,
    onNavigateToMy: () -> Unit,
) {
    val viewModel = koinViewModel<BookmarkViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                BookmarkSideEffect.NavigateToHome -> onNavigateToHome()
                BookmarkSideEffect.NavigateToProjectList -> onNavigateToProjectList()
                BookmarkSideEffect.NavigateToMy -> onNavigateToMy()
            }
        }
    }

    BookmarkScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
