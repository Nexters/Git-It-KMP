package com.nexters.hytime.gitit.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.repository.MemberRepository
import com.nexters.hytime.gitit.domain.usecase.SignInUseCase
import com.nexters.hytime.gitit.feature.onboarding.terms.TermsAgreementState
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 온보딩 화면의 단일 [OnboardingUiState]를 관리하는 ViewModel이다.
 *
 * 비즈니스 상태는 [setState]로만 변경하고, 일회성 부작용은 [events]로 흘려보낸다.
 *
 * @property signInUseCase 로그인 전체 흐름을 수행하는 유스케이스
 * @property memberRepository 회원 큐레이션을 저장하는 도메인 저장소
 */
class OnboardingViewModel(
    private val signInUseCase: SignInUseCase,
    private val memberRepository: MemberRepository,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    /**
     * 온보딩에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 로그인·약관·큐레이션 화면에서 발생한 의도
     */
    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.ToggleAllTerms -> toggleAllTerms()
            OnboardingIntent.ToggleServiceTerm -> toggleServiceTerm()
            OnboardingIntent.TogglePrivacyTerm -> togglePrivacyTerm()
            OnboardingIntent.ResetTerms -> setState { copy(termsAgreement = TermsAgreementState()) }
            OnboardingIntent.ConfirmTerms -> confirmTerms()
            is OnboardingIntent.CurationPositionSelected -> updateCuration { copy(position = intent.position, hasError = false) }
            is OnboardingIntent.CurationCareerLevelSelected -> updateCuration { copy(careerLevel = intent.careerLevel, hasError = false) }
            OnboardingIntent.CurationNext -> moveCurationForward()
            OnboardingIntent.CurationBack -> moveCurationBack()
        }
    }

    /**
     * 전체 동의 체크를 토글한다. 두 필수 약관을 한 번에 켜거나 끈다.
     */
    private fun toggleAllTerms() {
        setState {
            val newValue = !termsAgreement.isAllAgreed
            copy(
                termsAgreement =
                    TermsAgreementState(
                        isServiceAgreed = newValue,
                        isPrivacyAgreed = newValue,
                    ),
            )
        }
    }

    /**
     * 서비스 이용 약관 체크를 토글한다.
     */
    private fun toggleServiceTerm() {
        setState {
            copy(termsAgreement = termsAgreement.copy(isServiceAgreed = !termsAgreement.isServiceAgreed))
        }
    }

    /**
     * 개인정보 수집 약관 체크를 토글한다.
     */
    private fun togglePrivacyTerm() {
        setState {
            copy(termsAgreement = termsAgreement.copy(isPrivacyAgreed = !termsAgreement.isPrivacyAgreed))
        }
    }

    /**
     * 다음 버튼 인텐트. 필수 약관에 모두 동의한 경우 로그인을 수행한다.
     * 동의가 충분하지 않으면 아무 일도 일어나지 않는다.
     */
    private fun confirmTerms() {
        if (!_uiState.value.termsAgreement.isAllAgreed) return
        signIn()
    }

    /**
     * 구글 로그인을 수행한다. [LoginStep.Loading]으로 바꾼 뒤 유스케이스를 실행하고,
     * 성공 시 큐레이션 필요 여부에 따라 입력 화면 또는 홈 이동 이벤트를 제공한다.
     */
    private fun signIn() {
        if (_uiState.value.loginStep is LoginStep.Loading) return
        setState { copy(loginStep = LoginStep.Loading) }
        viewModelScope.launch {
            signInUseCase()
                .onSuccess { session ->
                    if (session.needsCuration) {
                        setState { copy(loginStep = LoginStep.Success, curation = CurationUiState()) }
                    } else {
                        setState { copy(loginStep = LoginStep.Success) }
                        _events.emit(OnboardingEvent.NavigateToHome)
                    }
                }.onFailure { error ->
                    logger.e(throwable = error) { "온보딩 로그인 실패" }
                    setState { copy(loginStep = LoginStep.Error) }
                }
        }
    }

    /** 현재 입력이 유효하면 다음 큐레이션 단계로 이동하거나 제출한다. */
    private fun moveCurationForward() {
        val curation = _uiState.value.curation ?: return
        if (curation.isSubmitting) return
        when (curation.step) {
            CurationStep.Position -> {
                if (curation.position == null) return
                updateCuration { copy(step = CurationStep.CareerLevel) }
            }

            CurationStep.CareerLevel -> submitCuration(curation)
        }
    }

    /** 제출 중이 아닐 때 이전 큐레이션 단계로 이동한다. */
    private fun moveCurationBack() {
        val curation = _uiState.value.curation ?: return
        if (curation.isSubmitting) return
        updateCuration {
            copy(
                step =
                    when (step) {
                        CurationStep.Position -> CurationStep.Position
                        CurationStep.CareerLevel -> CurationStep.Position
                    },
                hasError = false,
            )
        }
    }

    /**
     * 완성된 큐레이션 정보를 서버에 저장한다.
     *
     * @param curation 제출할 개발 분야·코드 이해 수준 상태
     */
    private fun submitCuration(curation: CurationUiState) {
        val position = curation.position ?: return
        val careerLevel = curation.careerLevel ?: return
        updateCuration { copy(isSubmitting = true, hasError = false) }
        viewModelScope.launch {
            memberRepository
                .curateMember(MemberCuration(position, careerLevel))
                .onSuccess {
                    updateCuration { copy(isSubmitting = false) }
                    _events.emit(OnboardingEvent.NavigateToIntermediateSplash)
                }.onFailure { error ->
                    logger.e(throwable = error) { "회원 큐레이션 등록 실패" }
                    updateCuration { copy(isSubmitting = false, hasError = true) }
                }
        }
    }

    /**
     * 큐레이션 하위 상태만 복사해 갱신한다.
     *
     * @param reducer 이전 큐레이션 상태를 새 상태로 변환하는 함수
     */
    private fun updateCuration(reducer: CurationUiState.() -> CurationUiState) {
        setState { copy(curation = curation?.reducer()) }
    }

    /**
     * [reducer] 블록으로 [OnboardingUiState]를 복사해 갱신한다.
     *
     * @param reducer 이전 상태를 새 상태로 변환하는 함수
     */
    private fun setState(reducer: OnboardingUiState.() -> OnboardingUiState) {
        _uiState.value = _uiState.value.reducer()
    }
}
