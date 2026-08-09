package com.nexters.hytime.gitit.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.SignInUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 온보딩 화면의 구글 로그인을 수행하는 ViewModel이다.
 *
 * [SignInUseCase]를 호출해 ID Token 획득부터 백엔드 검증까지 수행하고,
 * 결과를 [OnboardingUiState]로 노출한다.
 *
 * @property signInUseCase 로그인 전체 흐름을 수행하는 유스케이스
 */
class OnboardingViewModel(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    /**
     * 로그인 진행 상태. 외부에는 읽기 전용 [StateFlow]로 노출된다.
     */
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /**
     * 구글 로그인을 수행한다. 상태를 [OnboardingUiState.Loading]으로 바꾼 뒤 유스케이스를 실행한다.
     */
    fun signIn() {
        _uiState.value = OnboardingUiState.Loading
        viewModelScope.launch {
            signInUseCase()
                .onSuccess { account -> _uiState.value = OnboardingUiState.Success(account) }
                .onFailure { error ->
                    logger.e(throwable = error) { "온보딩 로그인 실패" }
                    _uiState.value = OnboardingUiState.Error(error.message ?: "알 수 없는 오류")
                }
        }
    }
}
