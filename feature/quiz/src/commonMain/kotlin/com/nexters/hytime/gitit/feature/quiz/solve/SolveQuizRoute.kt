package com.nexters.hytime.gitit.feature.quiz.solve

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 문제 풀이 상태와 일회성 이벤트를 화면에 연결한다.
 *
 * @param projectId 문제를 불러올 프로젝트 식별자
 * @param setId 문제를 특정 학습 세트로 제한할 때 사용하는 식별자
 * @param onBackClick 이전 화면으로 이동하는 콜백
 */
@Composable
fun SolveQuizRoute(
    projectId: String,
    setId: String? = null,
    onBackClick: () -> Unit,
) {
    val viewModel =
        koinViewModel<SolveQuizViewModel>(key = "$projectId:$setId") { parametersOf(SolveQuizArgs(projectId, setId)) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                SolveQuizSideEffect.NavigateBack -> onBackClick()
                is SolveQuizSideEffect.OpenUrl -> uriHandler.openUri(sideEffect.url)
            }
        }
    }

    SolveQuizScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
