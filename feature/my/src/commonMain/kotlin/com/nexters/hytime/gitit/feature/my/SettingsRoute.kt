package com.nexters.hytime.gitit.feature.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * 설정 화면의 진입점(Route)이다.
 *
 * 선택 화면에서 돌아올 때 최신 값을 표시하도록 화면이 나타날 때마다 프로필을 다시 조회한다.
 *
 * @param onBackClick 이전 화면으로 돌아가는 콜백
 * @param onPolicyClick 서비스 약관 및 정책 링크를 열도록 요청하는 콜백
 * @param onDeleteAccountClick 계정 삭제 안내 화면으로 이동하는 콜백
 * @param onDevelopmentFieldClick 개발 분야 선택 화면으로 이동하는 콜백
 * @param onLearningLevelClick 개발 수준 선택 화면으로 이동하는 콜백
 */
@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    onPolicyClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onDevelopmentFieldClick: () -> Unit,
    onLearningLevelClick: () -> Unit,
) {
    val viewModel = koinViewModel<SettingsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    SettingsScreen(
        developmentField = uiState.position?.let { stringResource(it.toDisplayLabelResource()) }.orEmpty(),
        learningLevel = uiState.careerLevel?.let { stringResource(it.toDisplayLabelResource()) }.orEmpty(),
        onBackClick = onBackClick,
        onPolicyClick = onPolicyClick,
        onDeleteAccountClick = onDeleteAccountClick,
        onDevelopmentFieldClick = onDevelopmentFieldClick,
        onLearningLevelClick = onLearningLevelClick,
    )
}
