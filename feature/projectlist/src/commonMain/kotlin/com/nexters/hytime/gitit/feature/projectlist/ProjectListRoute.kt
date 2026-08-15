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
 * @param onNavigateToHome 홈 화면으로 이동하는 콜백
 * @param onNavigateToMy 마이 화면으로 이동하는 콜백
 * @param onNavigateToBookmark 저장한 문제 화면으로 이동하는 콜백
 * @param onNavigateToQuiz 선택한 프로젝트의 문제 풀이 화면으로 이동하는 콜백
 */
@Composable
fun ProjectListRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToMy: () -> Unit,
    onNavigateToBookmark: () -> Unit,
    onNavigateToQuiz: (String) -> Unit,
) {
    val viewModel = viewModel { ProjectListViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                ProjectListSideEffect.NavigateToHome -> onNavigateToHome()
                ProjectListSideEffect.NavigateToMy -> onNavigateToMy()
                ProjectListSideEffect.NavigateToBookmark -> onNavigateToBookmark()
                is ProjectListSideEffect.NavigateToQuiz -> onNavigateToQuiz(sideEffect.projectId)
            }
        }
    }

    ProjectListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
