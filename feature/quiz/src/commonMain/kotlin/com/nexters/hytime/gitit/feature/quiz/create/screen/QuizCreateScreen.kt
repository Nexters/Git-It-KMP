package com.nexters.hytime.gitit.feature.quiz.create.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateIntent
import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateStage
import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateUiState
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizCreateReminderSheet

/**
 * 문제 생성 상태에 해당하는 화면을 선택해 표시한다.
 *
 * @param uiState 현재 문제 생성 상태
 * @param onIntent 사용자 입력을 ViewModel로 전달하는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun QuizCreateScreen(
    uiState: QuizCreateUiState,
    onIntent: (QuizCreateIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        when (uiState.stage) {
            QuizCreateStage.Knowledge ->
                QuizCreateKnowledgeScreen(
                    selected = uiState.knowledgeLevel,
                    onSelect = { onIntent(QuizCreateIntent.SelectKnowledge(it)) },
                    onBackClick = { onIntent(QuizCreateIntent.BackClick) },
                    onNextClick = { onIntent(QuizCreateIntent.NextClick) },
                )
            QuizCreateStage.Topics ->
                QuizCreateTopicsScreen(
                    selected = uiState.topics,
                    onToggle = { onIntent(QuizCreateIntent.ToggleTopic(it)) },
                    onBackClick = { onIntent(QuizCreateIntent.BackClick) },
                    onNextClick = { onIntent(QuizCreateIntent.NextClick) },
                )
            QuizCreateStage.Ready ->
                QuizCreateReadyScreen(
                    onBackClick = { onIntent(QuizCreateIntent.BackClick) },
                    onStartClick = { onIntent(QuizCreateIntent.StartGeneration) },
                )
            QuizCreateStage.Generating ->
                QuizCreateGeneratingScreen(
                    step = uiState.generationStep,
                    onWaitAtHomeClick = { onIntent(QuizCreateIntent.WaitAtHome) },
                )
        }

        if (uiState.showReminderPrompt) {
            QuizCreateReminderSheet(
                onEnableClick = { onIntent(QuizCreateIntent.EnableReminder) },
                onDismissClick = { onIntent(QuizCreateIntent.DismissReminder) },
            )
        }
    }
}

@Preview(name = "문제 생성 플로우", widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateScreenPreview() {
    GitItTheme {
        QuizCreateScreen(
            uiState =
                QuizCreateUiState(
                    projectId = "preview-project",
                    stage = QuizCreateStage.Knowledge,
                ),
            onIntent = {},
        )
    }
}
