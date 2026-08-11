package com.nexters.hytime.gitit.feature.projectlist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 프로젝트 리스트 화면의 상태와 사용자 의도를 관리한다.
 */
class ProjectListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectListUiState(projects = dummyProjects))

    /**
     * 프로젝트 리스트 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<ProjectListSideEffect>(extraBufferCapacity = 1)

    /**
     * 프로젝트 리스트 화면에서 한 번만 처리할 이벤트 스트림이다.
     */
    val sideEffects: SharedFlow<ProjectListSideEffect> = _sideEffects.asSharedFlow()

    /**
     * 프로젝트 리스트 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 프로젝트 리스트 화면 의도
     */
    fun onIntent(intent: ProjectListIntent) {
        when (intent) {
            ProjectListIntent.BackClick -> emit(ProjectListSideEffect.NavigateBack)
            ProjectListIntent.HomeTabClick -> emit(ProjectListSideEffect.NavigateToHome)
            ProjectListIntent.ProjectTabClick -> Unit
            ProjectListIntent.SavedTabClick -> emit(ProjectListSideEffect.NavigateToBookmark)
            ProjectListIntent.MyTabClick -> emit(ProjectListSideEffect.NavigateToMy)
            is ProjectListIntent.PlayProjectClick -> emit(ProjectListSideEffect.NavigateToQuiz)
        }
    }

    private fun emit(sideEffect: ProjectListSideEffect) {
        _sideEffects.tryEmit(sideEffect)
    }
}

/** domain/data 연동 전까지 화면에 표시할 더미 프로젝트 목록이다. */
private val dummyProjects =
    listOf(
        ProjectListItem(
            id = "now-in-android-1",
            title = "Now in\nAndroid",
            techStack = "Kotlin · Compose · Coroutines",
            setLabel = "Set 1",
            recentSetTitle = "Compose 핵심 개념",
            progress = 65,
            footerText = "설정",
            showPlayButton = true,
        ),
        ProjectListItem(
            id = "now-in-android-2",
            title = "Now in\nAndroid",
            techStack = "Kotlin · Compose · Coroutines",
            setLabel = "Set 1",
            recentSetTitle = "Compose 핵심 개념",
            progress = 65,
        ),
        ProjectListItem(
            id = "now-in-android-3",
            title = "Now in\nAndroid",
            techStack = "Kotlin · Compose · Coroutines",
            setLabel = "Set 1",
            recentSetTitle = "Compose 핵심 개념",
            progress = 65,
        ),
    )
