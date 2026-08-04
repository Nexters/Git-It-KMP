package com.nexters.hytime.gitit.feature.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 홈 화면의 상태와 사용자 의도를 관리한다.
 */
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())

    /**
     * 홈 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * 홈 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 홈 화면 의도
     */
    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Refresh -> Unit
        }
    }
}
