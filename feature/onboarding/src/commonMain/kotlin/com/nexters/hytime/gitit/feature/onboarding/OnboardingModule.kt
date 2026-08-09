package com.nexters.hytime.gitit.feature.onboarding

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * 온보딩 기능에서 필요한 Koin 의존성을 등록한다.
 */
val onboardingModule: Module =
    module {
        viewModel { OnboardingViewModel(get()) }
    }
