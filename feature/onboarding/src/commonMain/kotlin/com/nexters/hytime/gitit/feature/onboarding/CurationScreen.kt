package com.nexters.hytime.gitit.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.feature.onboarding.tutorial.TutorialOption
import com.nexters.hytime.gitit.feature.onboarding.tutorial.TutorialScreen
import git_it_kmp.feature.onboarding.generated.resources.Res
import git_it_kmp.feature.onboarding.generated.resources.curation_android
import git_it_kmp.feature.onboarding.generated.resources.curation_backend
import git_it_kmp.feature.onboarding.generated.resources.curation_career_entry
import git_it_kmp.feature.onboarding.generated.resources.curation_career_entry_description
import git_it_kmp.feature.onboarding.generated.resources.curation_career_helper
import git_it_kmp.feature.onboarding.generated.resources.curation_career_junior
import git_it_kmp.feature.onboarding.generated.resources.curation_career_junior_description
import git_it_kmp.feature.onboarding.generated.resources.curation_career_middle
import git_it_kmp.feature.onboarding.generated.resources.curation_career_middle_description
import git_it_kmp.feature.onboarding.generated.resources.curation_career_senior
import git_it_kmp.feature.onboarding.generated.resources.curation_career_senior_description
import git_it_kmp.feature.onboarding.generated.resources.curation_career_title
import git_it_kmp.feature.onboarding.generated.resources.curation_complete
import git_it_kmp.feature.onboarding.generated.resources.curation_frontend
import git_it_kmp.feature.onboarding.generated.resources.curation_ios
import git_it_kmp.feature.onboarding.generated.resources.curation_next
import git_it_kmp.feature.onboarding.generated.resources.curation_position_title
import git_it_kmp.feature.onboarding.generated.resources.curation_submit_error
import git_it_kmp.feature.onboarding.generated.resources.curation_submitting
import git_it_kmp.feature.onboarding.generated.resources.tutorial_preview_thumbnail
import org.jetbrains.compose.resources.stringResource

/**
 * 현재 큐레이션 단계에 맞는 입력 화면을 표시한다.
 *
 * @param state 개발 분야·코드 이해 수준 선택 상태
 * @param onIntent 사용자 입력을 ViewModel로 전달하는 단일 진입점
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
internal fun CurationContent(
    state: CurationUiState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.step) {
        CurationStep.Position ->
            TutorialScreen(
                title = stringResource(Res.string.curation_position_title),
                options = positionOptions(),
                selectedOptionId = state.position?.name,
                nextButtonText = stringResource(Res.string.curation_next),
                onOptionClick = { onIntent(OnboardingIntent.CurationPositionSelected(Position.valueOf(it.id))) },
                onBackClick = { onIntent(OnboardingIntent.CurationBack) },
                onNextClick = { onIntent(OnboardingIntent.CurationNext) },
                modifier = modifier,
                nextButtonEnabled = state.position != null && !state.isSubmitting,
            )

        CurationStep.CareerLevel ->
            TutorialScreen(
                title = stringResource(Res.string.curation_career_title),
                options = careerLevelOptions(),
                selectedOptionId = state.careerLevel?.name,
                nextButtonText =
                    stringResource(
                        if (state.isSubmitting) Res.string.curation_submitting else Res.string.curation_complete,
                    ),
                onOptionClick = { onIntent(OnboardingIntent.CurationCareerLevelSelected(CareerLevel.valueOf(it.id))) },
                onBackClick = { onIntent(OnboardingIntent.CurationBack) },
                onNextClick = { onIntent(OnboardingIntent.CurationNext) },
                modifier = modifier,
                helperText =
                    stringResource(
                        if (state.hasError) Res.string.curation_submit_error else Res.string.curation_career_helper,
                    ),
                nextButtonEnabled = state.careerLevel != null && !state.isSubmitting,
            )
    }
}

/** 개발 분야 선택 화면에 표시할 옵션을 만든다. */
@Composable
private fun positionOptions(): List<TutorialOption> =
    listOf(
        TutorialOption(Position.FRONTEND.name, stringResource(Res.string.curation_frontend), Res.drawable.tutorial_preview_thumbnail),
        TutorialOption(Position.BACKEND.name, stringResource(Res.string.curation_backend), Res.drawable.tutorial_preview_thumbnail),
        TutorialOption(Position.IOS.name, stringResource(Res.string.curation_ios), Res.drawable.tutorial_preview_thumbnail),
        TutorialOption(Position.ANDROID.name, stringResource(Res.string.curation_android), Res.drawable.tutorial_preview_thumbnail),
    )

/** 코드 이해 수준 선택 화면에 표시할 옵션을 만든다. */
@Composable
private fun careerLevelOptions(): List<TutorialOption> =
    listOf(
        TutorialOption(
            CareerLevel.ENTRY.name,
            stringResource(Res.string.curation_career_entry),
            Res.drawable.tutorial_preview_thumbnail,
            stringResource(Res.string.curation_career_entry_description),
        ),
        TutorialOption(
            CareerLevel.JUNIOR.name,
            stringResource(Res.string.curation_career_junior),
            Res.drawable.tutorial_preview_thumbnail,
            stringResource(Res.string.curation_career_junior_description),
        ),
        TutorialOption(
            CareerLevel.MIDDLE.name,
            stringResource(Res.string.curation_career_middle),
            Res.drawable.tutorial_preview_thumbnail,
            stringResource(Res.string.curation_career_middle_description),
        ),
        TutorialOption(
            CareerLevel.SENIOR.name,
            stringResource(Res.string.curation_career_senior),
            Res.drawable.tutorial_preview_thumbnail,
            stringResource(Res.string.curation_career_senior_description),
        ),
    )
