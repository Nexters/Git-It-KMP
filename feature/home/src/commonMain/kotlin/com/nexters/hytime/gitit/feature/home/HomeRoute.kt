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
 * @param isQuizCreating 앱 범위에서 문제 생성 세션이 진행 중인지 여부
 * @param onNavigateToProjectLoad 프로젝트로 등록할 저장소 확인 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 * @param onNavigateToProjectList 프로젝트 리스트 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 * @param onNavigateToMy 마이 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 * @param onNavigateToBookmark 저장한 문제 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 * @param onNavigateToProjectDetail 선택한 프로젝트의 상세 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 * @param onNavigateToQuiz 선택한 프로젝트의 문제 풀이 화면으로 이동하는 콜백. 전달하지 않으면 이동하지 않는다
 */
@Composable
fun HomeRoute(
    isQuizCreating: Boolean = false,
    onNavigateToProjectLoad: () -> Unit = {},
    onNavigateToProjectList: () -> Unit = {},
    onNavigateToMy: () -> Unit = {},
    onNavigateToBookmark: () -> Unit = {},
    onNavigateToProjectDetail: (String) -> Unit = {},
    onNavigateToQuiz: (String) -> Unit = {},
) {
    val viewModel = viewModel { HomeViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                HomeSideEffect.NavigateToProjectLoad -> onNavigateToProjectLoad()
                HomeSideEffect.NavigateToProjectList -> onNavigateToProjectList()
                HomeSideEffect.NavigateToMy -> onNavigateToMy()
                HomeSideEffect.NavigateToBookmark -> onNavigateToBookmark()
                is HomeSideEffect.NavigateToProjectDetail -> onNavigateToProjectDetail(sideEffect.projectId)
                is HomeSideEffect.NavigateToQuiz -> onNavigateToQuiz(sideEffect.projectId)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        isQuizCreating = isQuizCreating,
        onIntent = viewModel::onIntent,
    )
}
