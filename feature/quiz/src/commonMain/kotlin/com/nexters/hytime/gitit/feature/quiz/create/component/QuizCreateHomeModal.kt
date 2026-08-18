package com.nexters.hytime.gitit.feature.quiz.create.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateProgressState
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStatus
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.quiz_create_complete_action
import git_it_kmp.feature.quiz.generated.resources.quiz_create_complete_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_action
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_stop
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_title
import org.jetbrains.compose.resources.stringResource

/**
 * 홈 화면 위에 문제 생성 완료 또는 실패 상태를 Figma 모달로 표시한다.
 *
 * @param state 앱 범위 Store가 계산한 현재 생성 상태
 * @param onNextClick 완료된 프로젝트로 이동하는 콜백
 * @param onRetryClick 실패한 프로젝트 생성을 다시 시작하는 콜백
 * @param onStopClick 실패한 생성 세션을 종료하는 콜백
 * @param modifier 홈 전체 영역에 모달을 배치할 수식자
 */
@Composable
fun QuizCreateHomeModal(
    state: QuizCreateProgressState,
    onNextClick: () -> Unit,
    onRetryClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.status) {
        QuizCreateStatus.InProgress,
        QuizCreateStatus.Completing,
        QuizCreateStatus.Idle,
        QuizCreateStatus.Rejected,
        -> Unit
        QuizCreateStatus.Completed ->
            QuizCreateResultModal(
                isSuccess = true,
                onPrimaryClick = onNextClick,
                onSecondaryClick = onStopClick,
                modifier = modifier,
            )
        QuizCreateStatus.Failed ->
            QuizCreateResultModal(
                isSuccess = false,
                onPrimaryClick = onRetryClick,
                onSecondaryClick = onStopClick,
                modifier = modifier,
            )
    }
}

/**
 * 생성 성공 또는 실패 결과를 홈 위 하단 모달에 표시한다.
 *
 * @param isSuccess 성공 결과를 표시할지 여부
 * @param onPrimaryClick 프로젝트 확인 또는 재시도 콜백
 * @param onSecondaryClick 실패 세션 중단 콜백
 * @param modifier 홈 전체 영역에 모달을 배치할 수식자
 */
@Composable
private fun QuizCreateResultModal(
    isSuccess: Boolean,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val modalShape = RoundedCornerShape(16.dp)
    Box(modifier = modifier.fillMaxSize().background(GitItTheme.colors.black70)) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 19.dp, end = 19.dp, bottom = 57.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = modalShape,
                        ambientColor = Color.Black.copy(alpha = 0.35f),
                        spotColor = Color.Black.copy(alpha = 0.45f),
                    ).clip(modalShape)
                    .background(GitItTheme.colors.grey600)
                    .border(1.dp, GitItTheme.colors.grey500, modalShape)
                    .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 0.dp).padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                QuizCreateProjectThumbnail()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text =
                            stringResource(
                                if (isSuccess) {
                                    Res.string.quiz_create_complete_title
                                } else {
                                    Res.string.quiz_create_failed_title
                                },
                            ),
                        color = GitItTheme.colors.grey100,
                        style = GitItTheme.typography.title1,
                        textAlign = TextAlign.Center,
                    )
                    if (!isSuccess) {
                        Text(
                            text = stringResource(Res.string.quiz_create_failed_description),
                            color = GitItTheme.colors.grey400,
                            style = GitItTheme.typography.caption1,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                GitItButton(
                    text =
                        stringResource(
                            if (isSuccess) {
                                Res.string.quiz_create_complete_action
                            } else {
                                Res.string.quiz_create_failed_action
                            },
                        ),
                    onClick = onPrimaryClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (!isSuccess) {
                    GitItButton(
                        text = stringResource(Res.string.quiz_create_failed_stop),
                        onClick = onSecondaryClick,
                        style = GitItButtonStyle.Text,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

/** 홈 모달의 프로젝트 이미지 영역에 52dp placeholder를 표시한다. */
@Composable
private fun QuizCreateProjectThumbnail() {
    QuizCreateImagePlaceholder(
        modifier = Modifier.size(52.dp),
    )
}

@Preview(name = "홈 - 생성 완료", widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateHomeModalCompletedPreview() {
    GitItTheme {
        Box(modifier = Modifier.fillMaxSize().background(GitItTheme.colors.grey700)) {
            QuizCreateHomeModal(
                state =
                    QuizCreateProgressState(
                        projectId = "project-127",
                        status = QuizCreateStatus.Completed,
                        progressPercent = 100,
                    ),
                onNextClick = {},
                onRetryClick = {},
                onStopClick = {},
            )
        }
    }
}

@Preview(name = "홈 - 생성 실패", widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateHomeModalFailedPreview() {
    GitItTheme {
        Box(modifier = Modifier.fillMaxSize().background(GitItTheme.colors.grey700)) {
            QuizCreateHomeModal(
                state =
                    QuizCreateProgressState(
                        projectId = "project-127",
                        status = QuizCreateStatus.Failed,
                        progressPercent = 62,
                    ),
                onNextClick = {},
                onRetryClick = {},
                onStopClick = {},
            )
        }
    }
}
