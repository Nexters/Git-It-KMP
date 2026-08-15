package com.nexters.hytime.gitit.feature.projectdetail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 프로젝트 상세 화면의 단일 [ProjectDetailUiState]를 관리하는 ViewModel이다.
 *
 * 비즈니스 상태는 [setState]로만 변경하고, 일회성 부작용(네비게이션 등)은
 * [events]로 흘려보낸다. 현재는 더미 데이터를 채우기만 한다.
 *
 * @property projectId 네비게이션 인자로 전달된 프로젝트 식별자
 */
class ProjectDetailViewModel(
    val projectId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProjectDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProjectDetailEvent> = _events.asSharedFlow()

    init {
        loadDummy()
    }

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
     * 프로젝트 삭제 인텐트. 삭제 API 연동은 아직 미구현이다.
     */
    fun onDeleteProjectClick() {
        onDismissMoreMenu()
        // TODO: 삭제 유스케이스 연동 후 구현한다.
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

    private fun loadDummy() {
        setState {
            copy(
                project =
                    ProjectInfo(
                        name = "Nexters",
                        thumbnailUrl = "",
                        category = "Back-end",
                        difficulty = "입문",
                        starCount = "3.6k",
                        techStack = "Kotlin · Compose · Coroutines",
                    ),
                learningSets =
                    listOf(
                        LearningSetItem("1", "Set 1", "아이디어 PT 핵심 내용 확인하기"),
                        LearningSetItem("2", "Set 2", "서비스 문제와 타깃 알아보기"),
                        LearningSetItem("3", "Set 3", "아이디어별 해결 방식 비교하기"),
                        LearningSetItem("4", "Set 4", "주요 기능과 사용 경험 확인하기"),
                        LearningSetItem("5", "Set 5", "발표 아이디어 종합 정리하기"),
                    ),
                totalProgress = 0,
            )
        }
    }
}
