package com.nexters.hytime.gitit.feature.projectlist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
            ProjectListIntent.HomeTabClick -> emit(ProjectListSideEffect.NavigateToHome)
            ProjectListIntent.ProjectTabClick -> Unit
            ProjectListIntent.SavedTabClick -> emit(ProjectListSideEffect.NavigateToBookmark)
            ProjectListIntent.MyTabClick -> emit(ProjectListSideEffect.NavigateToMy)
            ProjectListIntent.DeleteMenuClick -> emit(ProjectListSideEffect.NavigateToProjectDelete)
            ProjectListIntent.DeleteModeBackClick -> emit(ProjectListSideEffect.NavigateBack)
            is ProjectListIntent.DeleteProjectClick -> {
                setState { copy(pendingDeleteProjectId = intent.projectId) }
            }
            ProjectListIntent.ConfirmDeleteClick ->
                _uiState.value.pendingDeleteProjectId?.let { projectId ->
                    setState {
                        copy(
                            projects = projects.filterNot { it.id == projectId },
                            pendingDeleteProjectId = null,
                        )
                    }
                }
            ProjectListIntent.DismissDeleteClick -> setState { copy(pendingDeleteProjectId = null) }
            is ProjectListIntent.ProjectClick -> emit(ProjectListSideEffect.NavigateToProjectDetail(intent.projectId))
            is ProjectListIntent.PlayProjectClick -> emit(ProjectListSideEffect.NavigateToQuiz(intent.projectId))
        }
    }

    /**
     * 현재 화면 상태를 원자적으로 갱신한다.
     *
     * @param reducer 이전 상태를 새 상태로 변환하는 함수
     */
    private fun setState(reducer: ProjectListUiState.() -> ProjectListUiState) {
        _uiState.update(reducer)
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
