package com.nexters.hytime.gitit.feature.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.domain.model.CareerLevel
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.illust_levels_beginner
import git_it_kmp.feature.my.generated.resources.illust_levels_junior
import git_it_kmp.feature.my.generated.resources.illust_levels_mid
import git_it_kmp.feature.my.generated.resources.illust_levels_senior
import git_it_kmp.feature.my.generated.resources.settings_career_level_entry_description
import git_it_kmp.feature.my.generated.resources.settings_career_level_junior_description
import git_it_kmp.feature.my.generated.resources.settings_career_level_middle_description
import git_it_kmp.feature.my.generated.resources.settings_career_level_senior_description
import git_it_kmp.feature.my.generated.resources.settings_development_level
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * 개발 수준 카드에 표시할 수준별 설명 리소스다. Figma 시안의 문구를 그대로 쓴다.
 *
 * @return 카드 제목 아래에 표시할 한 줄 설명 리소스
 */
private fun CareerLevel.toDescriptionResource(): StringResource =
    when (this) {
        CareerLevel.ENTRY -> Res.string.settings_career_level_entry_description
        CareerLevel.JUNIOR -> Res.string.settings_career_level_junior_description
        CareerLevel.MIDDLE -> Res.string.settings_career_level_middle_description
        CareerLevel.SENIOR -> Res.string.settings_career_level_senior_description
    }

/**
 * 개발 수준 카드 왼쪽에 표시할 수준별 일러스트 리소스다.
 *
 * @return 카드 썸네일 영역에 그릴 일러스트 리소스
 */
private fun CareerLevel.toThumbnailResource(): DrawableResource =
    when (this) {
        CareerLevel.ENTRY -> Res.drawable.illust_levels_beginner
        CareerLevel.JUNIOR -> Res.drawable.illust_levels_junior
        CareerLevel.MIDDLE -> Res.drawable.illust_levels_mid
        CareerLevel.SENIOR -> Res.drawable.illust_levels_senior
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
                    title = stringResource(careerLevel.toDisplayLabelResource()),
                    thumbnail = careerLevel.toThumbnailResource(),
                    description = stringResource(careerLevel.toDescriptionResource()),
                )
            },
        selectedOptionId = uiState.selected?.name,
        onOptionClick = { option -> viewModel.onCareerLevelClick(CareerLevel.valueOf(option.id)) },
        onBackClick = viewModel::onBackClick,
    )
}
