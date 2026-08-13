package com.nexters.hytime.gitit.feature.quiz.solve

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 문제 풀이 상태와 일회성 이벤트를 화면에 연결한다.
 *
 * @param projectId 문제를 불러올 프로젝트 식별자
 * @param setId 문제를 특정 학습 세트로 제한할 때 사용하는 식별자
 * @param onBackClick 이전 화면으로 이동하는 콜백
 */
@Composable
fun QuizRoute(
    projectId: String,
    setId: String? = null,
    onBackClick: () -> Unit,
) {
    val viewModel = viewModel(key = "$projectId:$setId") { QuizViewModel(projectId, setId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                QuizSideEffect.NavigateBack -> onBackClick()
                is QuizSideEffect.OpenUrl -> uriHandler.openUri(sideEffect.url)
            }
        }
    }

    QuizScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
