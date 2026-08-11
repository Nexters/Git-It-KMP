package com.nexters.hytime.gitit.feature.my

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
 * 마이 화면의 상태와 사용자 의도를 관리한다.
 */
class MyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(dummyMyUiState)

    /**
     * 마이 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<MySideEffect>(extraBufferCapacity = 1)

    /**
     * 마이 화면에서 한 번만 처리할 이벤트 스트림이다.
     */
    val sideEffects: SharedFlow<MySideEffect> = _sideEffects.asSharedFlow()

    /**
     * 마이 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 마이 화면 의도
     */
    fun onIntent(intent: MyIntent) {
        when (intent) {
            MyIntent.HomeTabClick -> emit(MySideEffect.NavigateToHome)
            MyIntent.ProjectTabClick -> emit(MySideEffect.NavigateToProjectList)
            MyIntent.SavedTabClick -> emit(MySideEffect.NavigateToBookmark)
            MyIntent.MyTabClick -> Unit
        }
    }

    private fun emit(sideEffect: MySideEffect) {
        viewModelScope.launch { _sideEffects.emit(sideEffect) }
    }
}

/** domain/data 연동 전까지 화면에 표시할 더미 마이 학습 상태다. */
private val dummyMyUiState =
    MyUiState(
        profile = MyProfile(name = "김이박", role = "Junior Developer"),
        stats =
            listOf(
                MyStudyStat(label = "이번 주", value = "13문제"),
                MyStudyStat(label = "이번 달", value = "47문제"),
                MyStudyStat(label = "연속 학습", value = "7일"),
            ),
        weeklyStudy =
            listOf(
                MyWeeklyStudy(day = "수", progress = 100),
                MyWeeklyStudy(day = "목", progress = 58),
                MyWeeklyStudy(day = "금", progress = 76),
                MyWeeklyStudy(day = "토", progress = 58),
                MyWeeklyStudy(day = "일", progress = 100),
                MyWeeklyStudy(day = "월", progress = 76),
                MyWeeklyStudy(day = "화", progress = 41),
            ),
    )
