package com.nexters.hytime.gitit.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.GetMemberProfileUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 마이 화면의 상태와 사용자 의도를 관리한다.
 *
 * @property getMemberProfile 회원 프로필과 학습 현황을 조회하는 유스케이스
 */
class MyViewModel(
    private val getMemberProfile: GetMemberProfileUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(MyUiState())

    /**
     * 마이 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<MySideEffect>(extraBufferCapacity = 1)

    /**
     * 마이 화면에서 한 번만 처리할 이벤트 스트림이다.
     */
    val sideEffects: SharedFlow<MySideEffect> = _sideEffects.asSharedFlow()

    init {
        loadProfile()
    }

    /**
     * 마이 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 마이 화면 의도
     */
    fun onIntent(intent: MyIntent) {
        when (intent) {
            MyIntent.SettingsClick -> emit(MySideEffect.NavigateToSettings)
            MyIntent.HomeTabClick -> emit(MySideEffect.NavigateToHome)
            MyIntent.ProjectTabClick -> emit(MySideEffect.NavigateToProjectList)
            MyIntent.SavedTabClick -> emit(MySideEffect.NavigateToBookmark)
            MyIntent.MyTabClick -> Unit
        }
    }

    /**
     * 회원 프로필을 조회해 화면 상태를 채운다.
     * 실패하면 빈 상태를 유지하고 원인을 로그로 남긴다.
     */
    private fun loadProfile() {
        viewModelScope.launch {
            getMemberProfile()
                .onSuccess { profile -> _uiState.value = profile.toUiState() }
                .onFailure { error -> logger.e(throwable = error) { "회원 프로필 조회 실패" } }
        }
    }

    private fun emit(sideEffect: MySideEffect) {
        viewModelScope.launch { _sideEffects.emit(sideEffect) }
    }
}
