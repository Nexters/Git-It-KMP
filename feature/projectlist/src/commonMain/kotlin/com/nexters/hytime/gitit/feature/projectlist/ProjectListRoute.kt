package com.nexters.hytime.gitit.feature.projectlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 프로젝트 리스트 화면의 진입점(Route)이다.
 *
 * @param onBackClick 이전 화면으로 이동하는 콜백. null이면 뒤로가기 버튼을 표시하지 않는다
 * @param onNavigateToHome 홈 화면으로 이동하는 콜백
 */
@Composable
fun ProjectListRoute(
    onBackClick: (() -> Unit)?,
    onNavigateToHome: () -> Unit,
) {
    val viewModel = viewModel { ProjectListViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                ProjectListSideEffect.NavigateBack -> onBackClick?.invoke()
                ProjectListSideEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    ProjectListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        showBackButton = onBackClick != null,
    )
}
