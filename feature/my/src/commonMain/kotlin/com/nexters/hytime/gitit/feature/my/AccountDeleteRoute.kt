package com.nexters.hytime.gitit.feature.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.account_delete_description
import git_it_kmp.feature.my.generated.resources.settings_delete_account
import org.jetbrains.compose.resources.stringResource

/**
 * 계정 삭제 안내 화면의 진입점이다.
 *
 * @param onBackClick 이전 화면으로 돌아가는 콜백
 * @param onDeleteAccountClick 계정을 즉시 삭제하도록 요청하는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun AccountDeleteRoute(
    onBackClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700)
                .statusBarsPadding(),
    ) {
        GitItTopBar(
            type = GitItTopBarType.LargeTitle,
            title = stringResource(Res.string.settings_delete_account),
            onBackClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(Res.string.account_delete_description),
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.body1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
        )
        Spacer(Modifier.weight(1f))
        GitItButton(
            text = stringResource(Res.string.settings_delete_account),
            onClick = onDeleteAccountClick,
            style = GitItButtonStyle.Text,
            state = GitItButtonState.Error,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp),
        )
    }
}

@Preview
@Composable
private fun AccountDeleteRoutePreview() {
    GitItTheme {
        AccountDeleteRoute(
            onBackClick = {},
            onDeleteAccountClick = {},
        )
    }
}
