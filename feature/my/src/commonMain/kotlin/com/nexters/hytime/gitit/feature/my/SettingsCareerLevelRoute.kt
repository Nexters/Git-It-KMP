package com.nexters.hytime.gitit.feature.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.domain.model.CareerLevel
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.settings_development_level
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * 개발 수준 카드에 표시할 수준별 설명이다. Figma 시안의 문구를 그대로 쓴다.
 *
 * @param careerLevel 설명을 찾을 개발 수준
 * @return 카드 제목 아래에 표시할 한 줄 설명
 */
private fun careerLevelDescription(careerLevel: CareerLevel): String =
    when (careerLevel) {
        CareerLevel.ENTRY -> "프로젝트 코드를 처음 살펴봐요."
        CareerLevel.JUNIOR -> "작은 기능 단위로 코드를 이해할 수 있어요."
        CareerLevel.MIDDLE -> "프로젝트 구조와 흐름을 함께 살펴봐요."
        CareerLevel.SENIOR -> "설계 의도와 변경 영향을 분석할 수 있어요."
    }

/**
 * 개발 수준 선택 화면의 진입점(Route)이다.
 *
 * 뒤로가기를 누르면 변경한 선택을 저장한 뒤 이전 화면으로 이동한다.
 *
 * @param onNavigateBack 이전 화면으로 돌아가는 콜백
 */
@Composable
fun SettingsCareerLevelRoute(onNavigateBack: () -> Unit) {
    val viewModel = koinViewModel<SettingsCareerLevelViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                SettingsCareerLevelSideEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    SettingsSelectionScreen(
        title = stringResource(Res.string.settings_development_level),
        options =
            CareerLevel.entries.map { careerLevel ->
                SettingsSelectionOption(
                    id = careerLevel.name,
                    title = careerLevel.toDisplayLabel(),
                    description = careerLevelDescription(careerLevel),
                )
            },
        selectedOptionId = uiState.selected?.name,
        onOptionClick = { option -> viewModel.onCareerLevelClick(CareerLevel.valueOf(option.id)) },
        onBackClick = viewModel::onBackClick,
    )
}
