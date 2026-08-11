package com.nexters.hytime.gitit.feature.questioncreate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.domain.model.GitHubRepository
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * 질문 생성용 저장소 확인 화면의 상태와 이벤트를 연결한다.
 *
 * @param onBackClick 이전 화면으로 이동하는 콜백
 * @param onRepositoryConfirmed 확인한 저장소로 학습 설정을 진행하는 콜백
 */
@Composable
fun QuestionCreateRoute(
    onBackClick: () -> Unit,
    onRepositoryConfirmed: (GitHubRepository) -> Unit,
) {
    val viewModel = koinViewModel<QuestionCreateViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                QuestionCreateEvent.NavigateBack -> onBackClick()
                is QuestionCreateEvent.RepositoryConfirmed -> onRepositoryConfirmed(event.repository)
            }
        }
    }

    QuestionCreateScreen(uiState = uiState, onIntent = viewModel::onIntent)
}
