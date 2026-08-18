package com.nexters.hytime.gitit.feature.projectdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.DeleteProjectUseCase
import com.nexters.hytime.gitit.domain.usecase.GetProjectDetailUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 프로젝트 상세 화면의 단일 [ProjectDetailUiState]를 관리하는 ViewModel이다.
 *
 * 비즈니스 상태는 [setState]로만 변경하고, 일회성 부작용(네비게이션 등)은
 * [events]로 흘려보낸다.
 *
 * @property projectId 네비게이션 인자로 전달된 프로젝트 식별자
 * @property getProjectDetail 프로젝트 상세 정보를 조회하는 유스케이스
 * @property deleteProject 프로젝트를 서버에서 삭제하는 유스케이스
 */
class ProjectDetailViewModel(
    val projectId: String,
    private val getProjectDetail: GetProjectDetailUseCase,
    private val deleteProject: DeleteProjectUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProjectDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProjectDetailEvent> = _events.asSharedFlow()

    /**
     * [reducer] 블록으로 [ProjectDetailUiState]를 복사해 갱신한다.
     */
    private fun setState(reducer: ProjectDetailUiState.() -> ProjectDetailUiState) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * 뒤로가기 인텐트. [ProjectDetailEvent.NavigateBack]을 흘려보낸다.
     */
    fun onBackClick() {
        emit(ProjectDetailEvent.NavigateBack)
    }

    /**
     * 더보기 메뉴 노출 상태를 토글한다.
     */
    fun onMoreMenuClick() {
        setState { copy(showMoreMenu = !showMoreMenu) }
    }

    /**
     * 더보기 메뉴를 닫는다.
     */
    fun onDismissMoreMenu() {
        setState { copy(showMoreMenu = false) }
    }

    /**
     * 저장한 문제 인텐트. [ProjectDetailEvent.NavigateToSavedQuestions]를 흘려보낸다.
     */
    fun onSavedQuestionsClick() {
        onDismissMoreMenu()
        emit(ProjectDetailEvent.NavigateToSavedQuestions)
    }

    /**
     * 프로젝트를 서버에서 삭제하고 성공하면 홈 화면 이동 이벤트를 흘려보낸다.
     * 실패하면 화면을 유지하고 원인을 로그로 남긴다.
     */
    fun onDeleteProjectClick() {
        viewModelScope.launch {
            deleteProject(projectId)
                .onSuccess { emit(ProjectDetailEvent.NavigateToHome) }
                .onFailure { error -> logger.e(throwable = error) { "프로젝트 삭제 실패" } }
        }
    }

    /**
     * 학습 세트 진입 인텐트. [ProjectDetailEvent.NavigateToLearningSet]을 흘려보낸다.
     *
     * @param setId 진입할 세트 식별자
     */
    fun onLearningSetClick(setId: String) {
        emit(ProjectDetailEvent.NavigateToLearningSet(setId))
    }

    /**
     * 문제풀이 바로가기 인텐트. [ProjectDetailEvent.NavigateToQuiz]를 흘려보낸다.
     */
    fun onQuestionSolvingClick() {
        emit(ProjectDetailEvent.NavigateToQuiz(projectId))
    }

    private fun emit(event: ProjectDetailEvent) {
        _events.tryEmit(event)
    }

    /**
     * 프로젝트 상세를 조회해 화면 상태를 채운다.
     * 실패하면 로딩 상태(project = null)를 유지하고 원인을 로그로 남긴다.
     */
    fun refresh() {
        viewModelScope.launch {
            getProjectDetail(projectId)
                .onSuccess { detail ->
                    setState {
                        copy(
                            project = detail.toProjectInfo(),
                            learningSets = detail.sets.map { it.toListItem() },
                            totalProgress = detail.overallProgressPercent,
                        )
                    }
                }.onFailure { error -> logger.e(throwable = error) { "프로젝트 상세 조회 실패" } }
        }
    }
}
