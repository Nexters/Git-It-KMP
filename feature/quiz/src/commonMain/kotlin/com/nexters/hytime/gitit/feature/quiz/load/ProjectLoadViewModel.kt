package com.nexters.hytime.gitit.feature.quiz.load

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.LoadGitHubRepositoryUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 저장소 링크 입력과 GitHub 검증 상태를 관리한다.
 *
 * @property loadGitHubRepository 저장소 링크를 검증하고 조회하는 UseCase
 */
class ProjectLoadViewModel(
    private val loadGitHubRepository: LoadGitHubRepositoryUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectLoadUiState())

    /** 화면이 구독할 읽기 전용 상태다. */
    val uiState: StateFlow<ProjectLoadUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProjectLoadEvent>(extraBufferCapacity = 1)

    /** 화면 밖에서 한 번만 처리할 이벤트 스트림이다. */
    val events: SharedFlow<ProjectLoadEvent> = _events.asSharedFlow()

    /**
     * 화면의 모든 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 의도
     */
    fun onIntent(intent: ProjectLoadIntent) {
        when (intent) {
            is ProjectLoadIntent.RepositoryUrlChanged -> setState { copy(repositoryUrl = intent.value, error = null) }
            ProjectLoadIntent.ClearRepositoryUrl -> setState { copy(repositoryUrl = "", error = null) }
            ProjectLoadIntent.LoadRepository -> loadRepository()
            ProjectLoadIntent.BackClick -> {
                if (uiState.value.repository == null) emit(ProjectLoadEvent.NavigateBack) else showInput()
            }
            ProjectLoadIntent.RejectRepository -> showInput()
            ProjectLoadIntent.ConfirmRepository -> {
                uiState.value.repository?.let { emit(ProjectLoadEvent.RepositoryConfirmed(it)) }
            }
        }
    }

    private fun loadRepository() {
        val repositoryUrl = uiState.value.repositoryUrl
        if (repositoryUrl.isBlank() || uiState.value.isLoading) return

        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            loadGitHubRepository(repositoryUrl)
                .onSuccess { repository -> setState { copy(repository = repository, isLoading = false) } }
                .onFailure { throwable ->
                    setState {
                        copy(
                            isLoading = false,
                            error = if (throwable is IllegalArgumentException) ProjectLoadError.InvalidUrl else null,
                        )
                    }
                }
        }
    }

    private fun showInput() {
        setState { copy(repository = null, error = null) }
    }

    private fun setState(reducer: ProjectLoadUiState.() -> ProjectLoadUiState) {
        _uiState.value = _uiState.value.reducer()
    }

    private fun emit(event: ProjectLoadEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
