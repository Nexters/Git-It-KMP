package com.nexters.hytime.gitit.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 */
class OnboardingViewModel(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    /**
     * [reducer] 블록으로 [OnboardingUiState]를 복사해 갱신한다.
     */
    private fun setState(reducer: OnboardingUiState.() -> OnboardingUiState) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * 전체 동의 체크를 토글한다. 두 필수 약관을 한 번에 켜거나 끈다.
     */
    fun toggleAllTerms() {
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
    fun toggleServiceTerm() {
        setState {
            copy(termsAgreement = termsAgreement.copy(isServiceAgreed = !termsAgreement.isServiceAgreed))
        }
    }

    /**
     * 개인정보 수집 약관 체크를 토글한다.
     */
    fun togglePrivacyTerm() {
        setState {
            copy(termsAgreement = termsAgreement.copy(isPrivacyAgreed = !termsAgreement.isPrivacyAgreed))
        }
    }

    /**
     * 약관 체크 상태를 초기화한다. 취소 시 호출된다.
     */
    fun resetTerms() {
        setState { copy(termsAgreement = TermsAgreementState()) }
    }

    /**
     * 다음 버튼 인텐트. 필수 약관에 모두 동의한 경우 로그인을 수행한다.
     * 동의가 충분하지 않으면 아무 일도 일어나지 않는다.
     */
    fun confirmTerms() {
        if (!_uiState.value.termsAgreement.isAllAgreed) return
        signIn()
    }

    /**
     * 구글 로그인을 수행한다. [LoginStep.Loading]으로 바꾼 뒤 유스케이스를 실행하고,
     * 성공 시 [OnboardingEvent.NavigateToHome]을 흘려보낸다.
     */
    fun signIn() {
        if (_uiState.value.loginStep is LoginStep.Loading) return
        setState { copy(loginStep = LoginStep.Loading) }
        viewModelScope.launch {
            signInUseCase()
                .onSuccess {
                    setState { copy(loginStep = LoginStep.Success) }
                    _events.emit(OnboardingEvent.NavigateToHome)
                }.onFailure { error ->
                    logger.e(throwable = error) { "온보딩 로그인 실패" }
                    setState { copy(loginStep = LoginStep.Error) }
                }
        }
    }
}
