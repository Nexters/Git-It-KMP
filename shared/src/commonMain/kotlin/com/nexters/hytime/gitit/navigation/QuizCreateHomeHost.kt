package com.nexters.hytime.gitit.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizCreateHomeModal
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateRetryHandler
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStatus
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStore
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * 홈 화면과 앱 범위 문제 생성 모달을 같은 레이어에 배치한다.
 *
 * @param onNavigateToProject 완료된 생성 세트의 프로젝트 상세로 이동하는 콜백
 * @param content 생성 진행 여부와 함께 표시할 홈 화면
 * @param modifier 홈과 모달이 차지할 전체 영역을 지정하는 수식자
 */
@Composable
internal fun QuizCreateHomeHost(
    onNavigateToProject: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (isQuizCreating: Boolean) -> Unit,
) {
    val createStore = koinInject<QuizCreateStore>()
    val retryHandler = koinInject<QuizCreateRetryHandler>()
    val state by createStore.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val isQuizCreating = state.status == QuizCreateStatus.InProgress || state.status == QuizCreateStatus.Completing

    Box(modifier = modifier.fillMaxSize()) {
        content(isQuizCreating)
        QuizCreateHomeModal(
            state = state,
            onNextClick = {
                state.projectId?.let { projectId ->
                    coroutineScope.launch {
                        createStore.cancel()
                        onNavigateToProject(projectId)
                    }
                }
            },
            onRetryClick = {
                coroutineScope.launch {
                    retryHandler.retry()
                }
            },
            onStopClick = { coroutineScope.launch { createStore.cancel() } },
        )
    }
}
