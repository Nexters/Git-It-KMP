package com.nexters.hytime.gitit.feature.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.domain.model.Position
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.settings_development_field
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** 개발 분야 선택 화면에 표시할 순서 고정 선택지다. */
private val positionOptions =
    listOf(
        Position.FRONTEND,
        Position.BACKEND,
        Position.IOS,
        Position.ANDROID,
    )

/**
 * 개발 분야 선택 화면의 진입점(Route)이다.
 *
 * 뒤로가기를 누르면 변경한 선택을 저장한 뒤 이전 화면으로 이동한다.
 *
 * @param onNavigateBack 이전 화면으로 돌아가는 콜백
 */
@Composable
fun SettingsPositionRoute(onNavigateBack: () -> Unit) {
    val viewModel = koinViewModel<SettingsPositionViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                SettingsPositionSideEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    SettingsSelectionScreen(
        title = stringResource(Res.string.settings_development_field),
        options =
            positionOptions.map { position ->
                SettingsSelectionOption(
                    id = position.name,
                    title = stringResource(position.toDisplayLabelResource()),
                )
            },
        selectedOptionId = uiState.selected?.name,
        onOptionClick = { option -> viewModel.onPositionClick(Position.valueOf(option.id)) },
        onBackClick = viewModel::onBackClick,
    )
}
