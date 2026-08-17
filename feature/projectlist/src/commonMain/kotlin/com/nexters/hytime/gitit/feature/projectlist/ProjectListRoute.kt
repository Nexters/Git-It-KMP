package com.nexters.hytime.gitit.feature.projectlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * 프로젝트 리스트 화면의 진입점(Route)이다.
 *
 * @param isDeleteMode 프로젝트 삭제 목적지인지 여부
 * @param onNavigateToProjectDelete 프로젝트 삭제 화면으로 이동하는 콜백
 * @param onBackClick 현재 화면을 닫는 콜백
 * @param onNavigateToHome 홈 화면으로 이동하는 콜백
 * @param onNavigateToMy 마이 화면으로 이동하는 콜백
 * @param onNavigateToBookmark 저장한 문제 화면으로 이동하는 콜백
 * @param onNavigateToProjectDetail 선택한 프로젝트의 상세 화면으로 이동하는 콜백
 * @param onNavigateToQuiz 선택한 프로젝트의 문제 풀이 화면으로 이동하는 콜백
 */
@Composable
fun ProjectListRoute(
    isDeleteMode: Boolean = false,
    onNavigateToProjectDelete: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMy: () -> Unit,
    onNavigateToBookmark: () -> Unit,
    onNavigateToProjectDetail: (String) -> Unit,
    onNavigateToQuiz: (String) -> Unit,
) {
    val viewModel = koinViewModel<ProjectListViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                ProjectListSideEffect.NavigateToProjectDelete -> onNavigateToProjectDelete()
                ProjectListSideEffect.NavigateBack -> onBackClick()
                ProjectListSideEffect.NavigateToHome -> onNavigateToHome()
                ProjectListSideEffect.NavigateToMy -> onNavigateToMy()
                ProjectListSideEffect.NavigateToBookmark -> onNavigateToBookmark()
                is ProjectListSideEffect.NavigateToProjectDetail -> onNavigateToProjectDetail(sideEffect.projectId)
                is ProjectListSideEffect.NavigateToQuiz -> onNavigateToQuiz(sideEffect.projectId)
            }
        }
    }

    ProjectListScreen(
        uiState = uiState,
        isDeleteMode = isDeleteMode,
        onIntent = viewModel::onIntent,
    )
}
