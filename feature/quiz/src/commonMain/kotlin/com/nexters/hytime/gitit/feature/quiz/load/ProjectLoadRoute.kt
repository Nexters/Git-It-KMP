package com.nexters.hytime.gitit.feature.quiz.load

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.domain.model.GitHubRepository
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * 프로젝트로 등록할 저장소 확인 화면의 상태와 이벤트를 연결한다.
 *
 * @param repositoryUrl 외부 공유로 전달되어 즉시 검증할 저장소 URL
 * @param onBackClick 이전 화면으로 이동하는 콜백
 * @param onRepositoryConfirmed 확인한 저장소로 학습 설정을 진행하는 콜백
 */
@Composable
fun ProjectLoadRoute(
    repositoryUrl: String = "",
    onBackClick: () -> Unit,
    onRepositoryConfirmed: (GitHubRepository) -> Unit,
) {
    val viewModel = koinViewModel<ProjectLoadViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(repositoryUrl) {
        if (repositoryUrl.isNotBlank() && uiState.repositoryUrl != repositoryUrl) {
            viewModel.onIntent(ProjectLoadIntent.RepositoryUrlChanged(repositoryUrl))
            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                ProjectLoadEvent.NavigateBack -> onBackClick()
                is ProjectLoadEvent.RepositoryConfirmed -> onRepositoryConfirmed(event.repository)
            }
        }
    }

    ProjectLoadScreen(uiState = uiState, onIntent = viewModel::onIntent)
}
