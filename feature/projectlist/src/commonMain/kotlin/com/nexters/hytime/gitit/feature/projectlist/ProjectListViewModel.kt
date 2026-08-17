package com.nexters.hytime.gitit.feature.projectlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.DeleteProjectUseCase
import com.nexters.hytime.gitit.domain.usecase.GetProjectsUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 프로젝트 리스트 화면의 상태와 사용자 의도를 관리한다.
 *
 * @property getProjects 등록한 프로젝트 목록을 조회하는 유스케이스
 * @property deleteProject 프로젝트를 서버에서 삭제하는 유스케이스
 */
class ProjectListViewModel(
    private val getProjects: GetProjectsUseCase,
    private val deleteProject: DeleteProjectUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(ProjectListUiState())

    /**
     * 프로젝트 리스트 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<ProjectListSideEffect>(extraBufferCapacity = 1)

    /**
     * 프로젝트 리스트 화면에서 한 번만 처리할 이벤트 스트림이다.
     */
    val sideEffects: SharedFlow<ProjectListSideEffect> = _sideEffects.asSharedFlow()

    init {
        loadProjects()
    }

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
                _uiState.value.pendingDeleteProjectId?.let(::deleteProjectOnServer)
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

    /**
     * 서버에서 프로젝트를 삭제하고 성공하면 목록에서도 제거한다.
     * 실패하면 목록을 유지하고 원인을 로그로 남긴다.
     *
     * @param projectId 삭제할 프로젝트 식별자
     */
    private fun deleteProjectOnServer(projectId: String) {
        viewModelScope.launch {
            deleteProject(projectId)
                .onSuccess {
                    setState {
                        copy(
                            projects = projects.filterNot { it.id == projectId },
                            pendingDeleteProjectId = null,
                        )
                    }
                }.onFailure { error ->
                    setState { copy(pendingDeleteProjectId = null) }
                    logger.e(throwable = error) { "프로젝트 삭제 실패" }
                }
        }
    }

    /**
     * 프로젝트 목록을 조회해 화면 상태를 채운다.
     * 실패하면 빈 목록을 유지하고 원인을 로그로 남긴다.
     */
    private fun loadProjects() {
        viewModelScope.launch {
            getProjects()
                .onSuccess { page -> setState { copy(projects = page.items.map { it.toListItem() }) } }
                .onFailure { error -> logger.e(throwable = error) { "프로젝트 목록 조회 실패" } }
        }
    }
}
