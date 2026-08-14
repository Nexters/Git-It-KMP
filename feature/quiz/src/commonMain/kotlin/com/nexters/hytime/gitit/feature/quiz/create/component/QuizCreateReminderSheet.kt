package com.nexters.hytime.gitit.feature.quiz.create.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.quiz_create_reminder_action
import git_it_kmp.feature.quiz.generated.resources.quiz_create_reminder_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_reminder_dismiss
import git_it_kmp.feature.quiz.generated.resources.quiz_create_reminder_title
import org.jetbrains.compose.resources.stringResource

/**
 * 홈에서 기다리기 전에 리마인드 알림 선택지를 표시한다.
 *
 * @param onEnableClick 리마인드 알림 설정 콜백
 * @param onDismissClick 알림 안내 닫기 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuizCreateReminderSheet(
    onEnableClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissClick,
        modifier = Modifier.fillMaxWidth(),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = GitItTheme.colors.grey600,
        contentColor = GitItTheme.colors.grey100,
        scrimColor = GitItTheme.colors.black70,
        tonalElevation = 0.dp,
        dragHandle = { QuizCreateReminderDragHandle() },
    ) {
        QuizCreateReminderSheetContent(
            onEnableClick = onEnableClick,
            onDismissClick = onDismissClick,
        )
    }
}

/**
 * 리마인드 시트의 artwork, 안내 문구와 액션을 표시한다.
 *
 * @param modifier 시트 내부 콘텐츠의 크기와 배치를 지정하는 수식자
 * @param onEnableClick 리마인드 알림 설정 콜백
 * @param onDismissClick 알림 안내 닫기 콜백
 */
@Composable
private fun QuizCreateReminderSheetContent(
    modifier: Modifier = Modifier,
    onEnableClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        QuizCreateImagePlaceholder(
            modifier = Modifier.size(128.dp),
            cornerRadius = 24.dp,
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(Res.string.quiz_create_reminder_title),
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.title1,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.quiz_create_reminder_description),
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.caption1,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        GitItButton(
            text = stringResource(Res.string.quiz_create_reminder_action),
            onClick = onEnableClick,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        GitItButton(
            text = stringResource(Res.string.quiz_create_reminder_dismiss),
            onClick = onDismissClick,
            style = GitItButtonStyle.Text,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Figma 리마인드 시트 상단의 36×4dp grabber를 표시한다. */
@Composable
private fun QuizCreateReminderDragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth().height(16.dp).padding(top = 5.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(GitItTheme.colors.grey400),
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateReminderSheetPreview() {
    GitItTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(GitItTheme.colors.grey700),
        ) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(GitItTheme.colors.grey600),
            ) {
                QuizCreateReminderDragHandle()
                QuizCreateReminderSheetContent(
                    onEnableClick = {},
                    onDismissClick = {},
                )
            }
        }
    }
}
