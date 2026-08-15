package com.nexters.hytime.gitit.feature.quiz.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.feature.quiz.create.screen.QuizCreateScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 문제 생성 상태와 화면 밖 이벤트를 연결한다.
 *
 * @param repositoryUrl 프로젝트로 등록할 GitHub 저장소 URL
 * @param onBackClick 이전 화면으로 이동하는 콜백
 * @param onNavigateHome 홈 화면으로 이동하는 콜백
 * @param onRequestNotificationPermission 리마인드 알림 권한을 요청하는 콜백
 */
@Composable
fun QuizCreateRoute(
    repositoryUrl: String,
    onBackClick: () -> Unit,
    onNavigateHome: () -> Unit,
    onRequestNotificationPermission: () -> Unit = {},
) {
    val viewModel =
        koinViewModel<QuizCreateViewModel>(
            key = "quiz-create:$repositoryUrl",
            parameters = { parametersOf(repositoryUrl) },
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                QuizCreateEvent.NavigateBack -> onBackClick()
                QuizCreateEvent.NavigateHome -> onNavigateHome()
                QuizCreateEvent.EnableReminderAndNavigateHome -> {
                    onRequestNotificationPermission()
                    onNavigateHome()
                }
            }
        }
    }

    QuizCreateScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
