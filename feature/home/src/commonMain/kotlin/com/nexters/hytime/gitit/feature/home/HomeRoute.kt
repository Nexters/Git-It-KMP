package com.nexters.hytime.gitit.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 홈 기능의 상태와 이벤트를 화면에 연결하는 진입점이다.
 *
 * 현재는 초기 내비게이션 구성을 검증하기 위해 화면만 표시한다.
 */
@Composable
fun HomeRoute() {
    val viewModel = viewModel { HomeViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(uiState = uiState)
}
