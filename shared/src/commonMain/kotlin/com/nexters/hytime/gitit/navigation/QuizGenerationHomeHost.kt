package com.nexters.hytime.gitit.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizGenerationHomeModal
import com.nexters.hytime.gitit.feature.quiz.create.generation.QuizGenerationCoordinator
import org.koin.compose.koinInject

/**
 * 홈 화면과 앱 범위 문제 생성 모달을 같은 레이어에 배치한다.
 *
 * @param onNavigateToProject 완료된 생성 세트의 프로젝트 상세로 이동하는 콜백
 * @param content 생성 모달 아래에 표시할 홈 화면
 * @param modifier 홈과 모달이 차지할 전체 영역을 지정하는 수식자
 */
@Composable
internal fun QuizGenerationHomeHost(
    onNavigateToProject: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val coordinator = koinInject<QuizGenerationCoordinator>()
    val state by coordinator.state.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        content()
        QuizGenerationHomeModal(
            state = state,
            onCloseClick = coordinator::hideHomeModal,
            onCancelClick = coordinator::cancel,
            onNextClick = {
                state.projectId?.let { projectId ->
                    coordinator.cancel()
                    onNavigateToProject(projectId)
                }
            },
            onRetryClick = {
                state.projectId?.let { projectId ->
                    coordinator.start(projectId)
                    coordinator.showHomeModal()
                }
            },
            onStopClick = coordinator::cancel,
        )
    }
}
