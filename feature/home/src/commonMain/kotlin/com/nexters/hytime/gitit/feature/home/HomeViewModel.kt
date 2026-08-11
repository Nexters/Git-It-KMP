package com.nexters.hytime.gitit.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.designsystem.GitItTheme
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
    private val _uiState = MutableStateFlow(HomeUiState(learningProjects = dummyLearningProjects))

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
            HomeIntent.LoadProjectClick,
            HomeIntent.ViewAllProjectsClick,
            -> emit(HomeSideEffect.NavigateToProjectList)
            is HomeIntent.LearningCardClick -> Unit
            is HomeIntent.LearningPlayClick -> Unit
            HomeIntent.ProjectTabClick -> emit(HomeSideEffect.NavigateToProjectList)
            HomeIntent.SavedTabClick -> emit(HomeSideEffect.NavigateToBookmark)
            HomeIntent.MyTabClick -> emit(HomeSideEffect.NavigateToMy)
        }
    }

    private fun emit(sideEffect: HomeSideEffect) {
        viewModelScope.launch { _sideEffects.emit(sideEffect) }
    }
}

/** 실제 데이터 연동 전 홈 카드 동작을 확인하기 위한 임시 목록이다. */
private val dummyLearningProjects =
    listOf(
        HomeLearningProject(
            id = "nexters",
            title = "Nexters",
            technologies = "Kotlin · Compose · Coroutines",
            setLabel = "Set 1",
            description = "Compose 핵심 개념",
            progress = 0.21f,
            backgroundColor = GitItTheme.colors.purple300,
        ),
        HomeLearningProject(
            id = "now-in-android",
            title = "Now in\nAndroid",
            technologies = "Kotlin · Compose · Coroutines",
            setLabel = "Set 1",
            description = "Compose 핵심 개념",
            progress = 0.21f,
            backgroundColor = GitItTheme.colors.blue100,
        ),
        HomeLearningProject(
            id = "compose-samples",
            title = "Compose\nSamples",
            technologies = "Kotlin · Compose",
            setLabel = "Set 2",
            description = "상태 관리 익히기",
            progress = 0.42f,
            backgroundColor = GitItTheme.colors.blue500,
        ),
    )
