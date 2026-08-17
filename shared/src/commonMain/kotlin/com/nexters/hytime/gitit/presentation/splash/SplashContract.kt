package com.nexters.hytime.gitit.presentation.splash

/**
 * 앱 시작 스플래시의 UI 상태다.
 *
 * @property isCheckingToken 저장된 액세스 토큰을 확인하고 있는지 여부
 */
data class SplashUiState(
    val isCheckingToken: Boolean = true,
)

/** 앱 시작 스플래시에서 한 번만 처리할 화면 이동 부작용이다. */
sealed interface SplashSideEffect {
    /** 유효한 로그인 세션으로 홈 화면에 진입한다. */
    data object NavigateToHome : SplashSideEffect

    /** 로그인 세션이 없거나 유효하지 않아 온보딩으로 이동한다. */
    data object NavigateToOnboarding : SplashSideEffect
}
