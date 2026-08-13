package com.nexters.hytime.gitit.feature.onboarding

import com.nexters.hytime.gitit.feature.onboarding.terms.TermsAgreementState

/**
 * 온보딩 화면의 단일 UI 상태(MVI State)다.
 *
 * 화면의 비즈니스 상태만 이 데이터 클래스 하나로 수렴한다.
 * 다이얼로그/바텀시트 노출 여부 등 순수 UI 제어 값은 Composable 로컬 상태에 둔다.
 *
 * @property loginStep 구글 로그인 진행 단계
 * @property termsAgreement 약관 동의 체크 상태
 */
data class OnboardingUiState(
    val loginStep: LoginStep = LoginStep.Idle,
    val termsAgreement: TermsAgreementState = TermsAgreementState(),
)

/**
 * 구글 로그인 진행 단계를 나타내는 봉인 인터페이스다.
 */
sealed interface LoginStep {
    /** 로그인을 아직 시도하지 않은 초기 상태다. */
    data object Idle : LoginStep

    /** 로그인 진행 중이다. 중복 요청을 막기 위해 버튼을 비활성화한다. */
    data object Loading : LoginStep

    /** 로그인에 성공한 상태다. */
    data object Success : LoginStep

    /**
     * 로그인에 실패한 상태다.
     *
     * @property message 사용자에게 보여줄 오류 메시지
     */
    data class Error(
        val message: String,
    ) : LoginStep
}

/**
 * 온보딩 화면에서 발생하는 일회성 부작용 이벤트다.
 */
sealed interface OnboardingEvent {
    /** 로그인 성공 후 홈 화면으로 이동한다. */
    data object NavigateToHome : OnboardingEvent
}
