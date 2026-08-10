package com.nexters.hytime.gitit.feature.onboarding

import androidx.compose.runtime.Composable

/**
 * 온보딩 화면의 진입점(Route)이다.
 *
 * 네비게이션 파라미터(savedStateHandle)를 읽어 Screen에 전달하는 역할만 한다.
 * ViewModel 생성, 상태 수집, 이벤트 구독은 [OnboardingScreen]이 담당한다.
 *
 * @param onNavigateToHome 로그인 성공 후 홈 화면으로 이동하는 콜백
 */
@Composable
fun OnboardingRoute(onNavigateToHome: () -> Unit) {
    OnboardingScreen(onNavigateToHome = onNavigateToHome)
}
