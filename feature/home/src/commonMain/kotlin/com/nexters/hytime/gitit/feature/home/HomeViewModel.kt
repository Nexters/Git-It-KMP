package com.nexters.hytime.gitit.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 홈 화면의 상태와 사용자 의도를 관리한다.
 */
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())

    /**
     * 홈 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<HomeSideEffect>(extraBufferCapacity = 1)

    /**
     * 홈 화면에서 한 번만 처리할 이벤트 스트림이다.
     */
    val sideEffects: SharedFlow<HomeSideEffect> = _sideEffects.asSharedFlow()

    /**
     * 홈 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 홈 화면 의도
     */
    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Refresh -> Unit
            HomeIntent.HomeTabClick -> Unit
            HomeIntent.ProjectTabClick -> emit(HomeSideEffect.NavigateToProjectList)
            HomeIntent.SavedTabClick -> {
                // TODO: 저장 화면 route 추가 후 연결한다.
            }
            HomeIntent.MyTabClick -> {
                // TODO: 마이 화면 route 추가 후 연결한다.
            }
        }
    }

    private fun emit(sideEffect: HomeSideEffect) {
        viewModelScope.launch { _sideEffects.emit(sideEffect) }
    }
}
