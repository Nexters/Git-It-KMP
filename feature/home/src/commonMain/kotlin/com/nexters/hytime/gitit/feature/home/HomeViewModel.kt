package com.nexters.hytime.gitit.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.GetProjectsUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 홈 화면의 상태와 사용자 의도를 관리한다.
 *
 * @property getProjects 이어서 학습할 프로젝트 목록을 조회하는 유스케이스
 */
class HomeViewModel(
    private val getProjects: GetProjectsUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

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

    init {
        loadProjects()
    }

    /**
     * 홈 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 홈 화면 의도
     */
    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Refresh -> loadProjects()
            HomeIntent.HomeTabClick -> Unit
            HomeIntent.LoadProjectClick -> emit(HomeSideEffect.NavigateToProjectLoad)
            HomeIntent.ViewAllProjectsClick -> emit(HomeSideEffect.NavigateToProjectList)
            is HomeIntent.LearningCardClick -> emit(HomeSideEffect.NavigateToProjectDetail(intent.projectId))
            is HomeIntent.LearningPlayClick -> emit(HomeSideEffect.NavigateToQuiz(intent.projectId))
            HomeIntent.ProjectTabClick -> emit(HomeSideEffect.NavigateToProjectList)
            HomeIntent.SavedTabClick -> emit(HomeSideEffect.NavigateToBookmark)
            HomeIntent.MyTabClick -> emit(HomeSideEffect.NavigateToMy)
        }
    }

    private fun emit(sideEffect: HomeSideEffect) {
        _sideEffects.tryEmit(sideEffect)
    }

    /**
     * 프로젝트 목록을 조회해 학습 카드를 채운다.
     * 실패하면 이전 목록을 유지하고 원인을 로그로 남긴다.
     */
    private fun loadProjects() {
        viewModelScope.launch {
            getProjects()
                .onSuccess { page ->
                    _uiState.value = _uiState.value.copy(learningProjects = page.items.map { it.toLearningProject() })
                }.onFailure { error -> logger.e(throwable = error) { "홈 프로젝트 목록 조회 실패" } }
        }
    }
}
