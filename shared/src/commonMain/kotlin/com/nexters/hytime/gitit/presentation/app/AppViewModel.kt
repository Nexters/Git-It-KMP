package com.nexters.hytime.gitit.presentation.app

import androidx.lifecycle.ViewModel
import com.nexters.hytime.gitit.domain.usecase.HasLoginSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 저장된 세션을 기준으로 앱의 초기 인증 상태를 관리한다.
 *
 * @property hasLoginSession 저장된 로그인 세션 존재 여부를 확인한다
 */
class AppViewModel(
    hasLoginSession: HasLoginSessionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState(isSignedIn = hasLoginSession()))

    /** 외부에 읽기 전용으로 노출하는 앱 인증 상태다. */
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
}
