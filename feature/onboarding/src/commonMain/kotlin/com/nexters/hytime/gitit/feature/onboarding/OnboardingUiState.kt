package com.nexters.hytime.gitit.feature.onboarding

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.feature.onboarding.terms.TermsAgreementState

/**
 * 온보딩 화면의 단일 UI 상태(MVI State)다.
 *
 * 화면의 비즈니스 상태만 이 데이터 클래스 하나로 수렴한다.
 * 다이얼로그/바텀시트 노출 여부 등 순수 UI 제어 값은 Composable 로컬 상태에 둔다.
 *
 * @property loginStep 구글 로그인 진행 단계
 * @property termsAgreement 약관 동의 체크 상태
 * @property curation 큐레이션이 필요한 회원의 입력 상태. 일반 로그인 화면에서는 `null`
 */
data class OnboardingUiState(
    val loginStep: LoginStep = LoginStep.Idle,
    val termsAgreement: TermsAgreementState = TermsAgreementState(),
    val curation: CurationUiState? = null,
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

    /** 로그인에 실패한 상태다. */
    data object Error : LoginStep
}

/**
 * 온보딩 화면에서 발생하는 일회성 부작용 이벤트다.
 */
sealed interface OnboardingEvent {
    /** 로그인 성공 후 홈 화면으로 이동한다. */
    data object NavigateToHome : OnboardingEvent

    /** 큐레이션 완료 후 중간 스플래시 화면으로 이동한다. */
    data object NavigateToIntermediateSplash : OnboardingEvent
}

/** 큐레이션 입력 순서다. */
enum class CurationStep {
    /** 학습할 개발 분야를 선택하는 단계다. */
    Position,

    /** 현재 코드 이해 수준을 선택하는 단계다. */
    CareerLevel,
}

/**
 * 최초 로그인 회원의 큐레이션 입력 상태다.
 *
 * @property step 현재 입력 단계
 * @property position 학습할 개발 분야
 * @property careerLevel 현재 코드 이해 수준
 * @property isSubmitting 서버에 등록 중인지 여부
 * @property hasError 마지막 등록 요청이 실패했는지 여부
 */
data class CurationUiState(
    val step: CurationStep = CurationStep.Position,
    val position: Position? = null,
    val careerLevel: CareerLevel? = null,
    val isSubmitting: Boolean = false,
    val hasError: Boolean = false,
)

/** 온보딩 화면에서 발생하는 사용자 의도다. */
sealed interface OnboardingIntent {
    /** 모든 필수 약관 선택 상태를 전환한다. */
    data object ToggleAllTerms : OnboardingIntent

    /** 서비스 이용 약관 선택 상태를 전환한다. */
    data object ToggleServiceTerm : OnboardingIntent

    /** 개인정보 처리 약관 선택 상태를 전환한다. */
    data object TogglePrivacyTerm : OnboardingIntent

    /** 약관 선택을 초기화한다. */
    data object ResetTerms : OnboardingIntent

    /** 약관 동의 후 Google 로그인을 시작한다. */
    data object ConfirmTerms : OnboardingIntent

    /**
     * 학습할 개발 분야를 선택한다.
     *
     * @property position 선택한 개발 분야
     */
    data class CurationPositionSelected(
        val position: Position,
    ) : OnboardingIntent

    /**
     * 현재 코드 이해 수준을 선택한다.
     *
     * @property careerLevel 선택한 코드 이해 수준
     */
    data class CurationCareerLevelSelected(
        val careerLevel: CareerLevel,
    ) : OnboardingIntent

    /** 큐레이션의 다음 단계로 이동하거나 마지막 입력을 제출한다. */
    data object CurationNext : OnboardingIntent

    /** 큐레이션의 이전 단계로 이동한다. */
    data object CurationBack : OnboardingIntent
}
