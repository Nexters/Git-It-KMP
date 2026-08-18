package com.nexters.hytime.gitit.feature.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 저장한 문제 화면의 진입점(Route)이다.
 *
 * @param projectId 처음 적용할 프로젝트 필터. null이면 전체 프로젝트를 표시한다
 * @param onNavigateToHome 홈 화면으로 이동하는 콜백
 * @param onNavigateToProjectList 프로젝트 리스트 화면으로 이동하는 콜백
 * @param onNavigateToMy 마이 화면으로 이동하는 콜백
 * @param onNavigateToQuiz 저장한 문제 하나를 푸는 화면으로 이동하는 콜백
 */
@Composable
fun BookmarkRoute(
    projectId: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToProjectList: () -> Unit,
    onNavigateToMy: () -> Unit,
    onNavigateToQuiz: (projectId: String, setId: String, questionId: String) -> Unit,
) {
    val viewModel = koinViewModel<BookmarkViewModel>(parameters = { parametersOf(projectId) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                BookmarkSideEffect.NavigateToHome -> onNavigateToHome()
                BookmarkSideEffect.NavigateToProjectList -> onNavigateToProjectList()
                BookmarkSideEffect.NavigateToMy -> onNavigateToMy()
                is BookmarkSideEffect.NavigateToQuiz ->
                    onNavigateToQuiz(sideEffect.projectId, sideEffect.setId, sideEffect.questionId)
            }
        }
    }

    BookmarkScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
