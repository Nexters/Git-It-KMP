package com.nexters.hytime.gitit.feature.projectdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 프로젝트 상세 화면의 진입점(Route)이다.
 *
 * [projectId]로 [ProjectDetailViewModel]을 만들고, 상태와 이벤트를 화면에 연결한다.
 *
 * @param projectId 네비게이션 인자로 전달된 프로젝트 식별자
 * @param onBackClick 뒤로가기 이벤트 콜백
 * @param onNavigateToSavedQuestions 저장한 문제 화면으로 이동하는 콜백
 * @param onNavigateToLearningSet 학습 세트 화면으로 이동하는 콜백
 * @param onNavigateToQuiz 문제 풀이 화면으로 이동하는 콜백
 */
@Composable
fun ProjectDetailRoute(
    projectId: String,
    onBackClick: () -> Unit,
    onNavigateToSavedQuestions: () -> Unit,
    onNavigateToLearningSet: (String) -> Unit,
    onNavigateToQuiz: () -> Unit,
) {
    val viewModel =
        koinViewModel<ProjectDetailViewModel>(
            parameters = { parametersOf(projectId) },
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                ProjectDetailEvent.NavigateBack -> onBackClick()
                ProjectDetailEvent.NavigateToSavedQuestions -> onNavigateToSavedQuestions()
                ProjectDetailEvent.NavigateToQuiz -> onNavigateToQuiz()
                is ProjectDetailEvent.NavigateToLearningSet -> onNavigateToLearningSet(event.setId)
            }
        }
    }

    ProjectDetailScreen(
        uiState = uiState,
        onBackClick = viewModel::onBackClick,
        onMoreMenuClick = viewModel::onMoreMenuClick,
        onDismissMoreMenu = viewModel::onDismissMoreMenu,
        onSavedQuestionsClick = viewModel::onSavedQuestionsClick,
        onQuestionSolvingClick = viewModel::onQuestionSolvingClick,
        onQuestionSolvingShortcutClick = viewModel::onQuestionSolvingShortcutClick,
        onDeleteProjectClick = viewModel::onDeleteProjectClick,
        onLearningSetClick = viewModel::onLearningSetClick,
        onReviewStartClick = viewModel::onReviewStartClick,
    )
}
