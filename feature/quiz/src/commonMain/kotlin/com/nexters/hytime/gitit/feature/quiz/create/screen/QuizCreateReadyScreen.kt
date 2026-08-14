package com.nexters.hytime.gitit.feature.quiz.create.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.quiz_create_ready_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_ready_start
import git_it_kmp.feature.quiz.generated.resources.quiz_create_ready_title
import org.jetbrains.compose.resources.stringResource

/**
 * 학습 세트 생성 시작 전 예상 소요 시간을 안내한다.
 *
 * @param onBackClick 이전 화면 이동 콜백
 * @param onStartClick 생성 시작 콜백
 */
@Composable
internal fun QuizCreateReadyScreen(
    onBackClick: () -> Unit,
    onStartClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        GitItTopBar(
            type = GitItTopBarType.Default,
            onBackClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp),
        )
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(bottom = 52.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.quiz_create_ready_title),
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.title1,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.quiz_create_ready_description),
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.body2,
                )
            }
        }
        GitItButton(
            text = stringResource(Res.string.quiz_create_ready_start),
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateReadyPreview() {
    GitItTheme {
        QuizCreateReadyScreen(onBackClick = {}, onStartClick = {})
    }
}
