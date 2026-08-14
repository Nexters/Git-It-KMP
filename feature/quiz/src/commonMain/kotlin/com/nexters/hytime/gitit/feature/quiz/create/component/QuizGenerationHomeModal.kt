package com.nexters.hytime.gitit.feature.quiz.create.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.nexters.hytime.gitit.designsystem.button.GitItButtonSize
import com.nexters.hytime.gitit.designsystem.button.GitItButtonState
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import com.nexters.hytime.gitit.feature.quiz.create.generation.QuizGenerationState
import com.nexters.hytime.gitit.feature.quiz.create.generation.QuizGenerationStatus
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.quiz_create_close
import git_it_kmp.feature.quiz.generated.resources.quiz_create_complete_action
import git_it_kmp.feature.quiz.generated.resources.quiz_create_complete_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_action
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_stop
import git_it_kmp.feature.quiz.generated.resources.quiz_create_failed_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_home_cancel
import git_it_kmp.feature.quiz.generated.resources.quiz_create_home_generating
import git_it_kmp.feature.quiz.generated.resources.quiz_create_next
import org.jetbrains.compose.resources.stringResource

/**
 * 홈 화면 위에 문제 생성 진행, 완료 또는 실패 상태를 Figma 모달로 표시한다.
 *
 * @param state 앱 범위 코디네이터가 계산한 현재 생성 상태
 * @param onCloseClick 생성 세션을 유지한 채 진행 모달을 닫는 콜백
 * @param onCancelClick 진행 중인 생성 세션을 취소하는 콜백
 * @param onNextClick 완료된 프로젝트로 이동하는 콜백
 * @param onRetryClick 실패한 프로젝트 생성을 다시 시작하는 콜백
 * @param onStopClick 실패한 생성 세션을 종료하는 콜백
 * @param modifier 홈 전체 영역에 모달을 배치할 수식자
 */
@Composable
fun QuizGenerationHomeModal(
    state: QuizGenerationState,
    onCloseClick: () -> Unit,
    onCancelClick: () -> Unit,
    onNextClick: () -> Unit,
    onRetryClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!state.isHomeModalVisible || state.status == QuizGenerationStatus.Idle) return

    when (state.status) {
        QuizGenerationStatus.Generating,
        QuizGenerationStatus.Completing,
        ->
            QuizGenerationProgressModal(
                state = state,
                onCloseClick = onCloseClick,
                onCancelClick = onCancelClick,
                modifier = modifier,
            )
        QuizGenerationStatus.Completed ->
            QuizGenerationResultModal(
                isSuccess = true,
                onPrimaryClick = onNextClick,
                onSecondaryClick = onStopClick,
                modifier = modifier,
            )
        QuizGenerationStatus.Failed ->
            QuizGenerationResultModal(
                isSuccess = false,
                onPrimaryClick = onRetryClick,
                onSecondaryClick = onStopClick,
                modifier = modifier,
            )
        QuizGenerationStatus.Idle -> Unit
    }
}

/**
 * 생성 중 상태의 썸네일, 진행률과 제어 버튼을 하단 모달에 표시한다.
 *
 * @param state 표시할 생성 진행 상태
 * @param onCloseClick 모달 닫기 콜백
 * @param onCancelClick 생성 취소 콜백
 * @param modifier 홈 전체 영역에 모달을 배치할 수식자
 */
@Composable
private fun QuizGenerationProgressModal(
    state: QuizGenerationState,
    onCloseClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val modalShape = RoundedCornerShape(16.dp)
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 19.dp, end = 19.dp, bottom = 107.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = modalShape,
                        ambientColor = Color.Black.copy(alpha = 0.35f),
                        spotColor = Color.Black.copy(alpha = 0.45f),
                    ).clip(modalShape)
                    .background(GitItTheme.colors.grey600)
                    .border(1.dp, GitItTheme.colors.grey500, modalShape)
                    .padding(top = 20.dp, bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                QuizGenerationProjectThumbnail()
                GitItButton(
                    text = stringResource(Res.string.quiz_create_close),
                    onClick = onCloseClick,
                    size = GitItButtonSize.Medium,
                    style = GitItButtonStyle.Secondary,
                    modifier = Modifier.width(58.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            QuizGenerationProgress(state = state)
            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                GitItButton(
                    text = stringResource(Res.string.quiz_create_home_cancel),
                    onClick = onCancelClick,
                    style = GitItButtonStyle.Secondary,
                    modifier = Modifier.weight(1f),
                )
                GitItButton(
                    text = stringResource(Res.string.quiz_create_next),
                    onClick = {},
                    state = GitItButtonState.Disabled,
                    modifier = Modifier.weight(1f),
                )
            }
        }
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
private fun QuizGenerationResultModal(
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
                QuizGenerationProjectThumbnail()
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
private fun QuizGenerationProjectThumbnail() {
    QuizCreateImagePlaceholder(
        modifier = Modifier.size(52.dp),
    )
}

/**
 * 홈 모달의 진행률 레이블과 7dp 막대를 표시한다.
 *
 * @param state 표시할 생성 진행 상태
 */
@Composable
private fun QuizGenerationProgress(state: QuizGenerationState) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.quiz_create_home_generating),
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.caption1,
            )
            Text(
                text = "${state.progressPercent}%",
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.caption1.copy(fontFeatureSettings = "tnum"),
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(state.progressPercent.coerceIn(0, 100) / 100f)
                        .height(7.dp)
                        .background(GitItTheme.colors.blue200),
            )
        }
    }
}

@Preview(name = "홈 - 생성 중", widthDp = 360, heightDp = 800)
@Composable
private fun QuizGenerationHomeModalPreview() {
    GitItTheme {
        Box(modifier = Modifier.fillMaxSize().background(GitItTheme.colors.grey700)) {
            QuizGenerationHomeModal(
                state =
                    QuizGenerationState(
                        projectId = "project-127",
                        status = QuizGenerationStatus.Generating,
                        progressPercent = 38,
                        isHomeModalVisible = true,
                    ),
                onCloseClick = {},
                onCancelClick = {},
                onNextClick = {},
                onRetryClick = {},
                onStopClick = {},
            )
        }
    }
}

@Preview(name = "홈 - 생성 완료", widthDp = 360, heightDp = 800)
@Composable
private fun QuizGenerationHomeModalCompletedPreview() {
    GitItTheme {
        Box(modifier = Modifier.fillMaxSize().background(GitItTheme.colors.grey700)) {
            QuizGenerationHomeModal(
                state =
                    QuizGenerationState(
                        projectId = "project-127",
                        status = QuizGenerationStatus.Completed,
                        progressPercent = 100,
                        isHomeModalVisible = true,
                    ),
                onCloseClick = {},
                onCancelClick = {},
                onNextClick = {},
                onRetryClick = {},
                onStopClick = {},
            )
        }
    }
}

@Preview(name = "홈 - 생성 실패", widthDp = 360, heightDp = 800)
@Composable
private fun QuizGenerationHomeModalFailedPreview() {
    GitItTheme {
        Box(modifier = Modifier.fillMaxSize().background(GitItTheme.colors.grey700)) {
            QuizGenerationHomeModal(
                state =
                    QuizGenerationState(
                        projectId = "project-127",
                        status = QuizGenerationStatus.Failed,
                        progressPercent = 62,
                        isHomeModalVisible = true,
                    ),
                onCloseClick = {},
                onCancelClick = {},
                onNextClick = {},
                onRetryClick = {},
                onStopClick = {},
            )
        }
    }
}
