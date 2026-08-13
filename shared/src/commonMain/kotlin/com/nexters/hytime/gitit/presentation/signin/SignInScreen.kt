package com.nexters.hytime.gitit.presentation.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * Google 로그인 화면이다.
 *
 * [SignInViewModel]을 통해 로그인을 수행하고, [SignInUiState]에 따라
 * 버튼 활성화, 로딩 표시, 결과 메시지를 렌더한다.
 *
 * @param viewModel 로그인 상태 관리 ViewModel
 */
@Composable
fun SignInScreen(viewModel: SignInViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = viewModel::signIn,
            enabled = state !is SignInUiState.Loading,
        ) {
            Text("Google 로그인")
        }
        Spacer(Modifier.height(8.dp))
        when (val current = state) {
            is SignInUiState.Idle -> Unit
            is SignInUiState.Loading -> CircularProgressIndicator()
            is SignInUiState.Success -> Text("로그인 성공")
            is SignInUiState.Error -> Text("로그인 실패: ${current.message}")
        }
    }
}
