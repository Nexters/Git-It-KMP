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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonState
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.quiz_create_next
import org.jetbrains.compose.resources.stringResource

/**
 * 선택형 화면의 공통 상단바, 본문과 하단 버튼을 배치한다.
 *
 * @param title 화면의 질문 제목
 * @param onBackClick 이전 단계 이동 콜백
 * @param buttonEnabled 다음 버튼 활성 여부
 * @param onNextClick 다음 단계 이동 콜백
 * @param description 제목 아래에 표시할 선택적 설명
 * @param content 선택 항목을 표시할 본문
 */
@Composable
internal fun QuizCreateSelectionScaffold(
    title: String,
    onBackClick: () -> Unit,
    buttonEnabled: Boolean,
    onNextClick: () -> Unit,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        GitItTopBar(
            type = GitItTopBarType.Default,
            onBackClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp),
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.title1,
            )
            description?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.caption1,
                )
            }
            Spacer(Modifier.height(48.dp))
            Box(modifier = Modifier.weight(1f)) { content() }
        }
        GitItButton(
            text = stringResource(Res.string.quiz_create_next),
            onClick = onNextClick,
            state = if (buttonEnabled) GitItButtonState.Default else GitItButtonState.Disabled,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(24.dp))
    }
}

/** 선택 카드의 프로젝트 이미지 영역에 임시 placeholder를 표시한다. */
@Composable
internal fun QuizCreateSelectionThumbnail() {
    QuizCreateImagePlaceholder(
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview(name = "선택 화면 공통 레이아웃", widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateSelectionScaffoldPreview() {
    GitItTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(GitItTheme.colors.grey700),
        ) {
            QuizCreateSelectionScaffold(
                title = "문제로 다루고 싶은 내용을\n모두 선택해주세요.",
                description = "선택한 내용들을 중심으로 문제가 생성돼요.",
                onBackClick = {},
                buttonEnabled = true,
                onNextClick = {},
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(GitItTheme.colors.grey600),
                )
            }
        }
    }
}
