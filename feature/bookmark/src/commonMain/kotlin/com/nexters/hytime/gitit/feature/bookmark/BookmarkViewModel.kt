package com.nexters.hytime.gitit.feature.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.BookmarkQuestionUseCase
import com.nexters.hytime.gitit.domain.usecase.GetBookmarkedQuestionsUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 저장한 문제 화면의 상태와 사용자 의도를 관리한다.
 *
 * @property getBookmarkedQuestions 북마크한 문제 목록을 조회하는 유스케이스
 * @property bookmarkQuestion 문제의 북마크 상태를 서버에 설정하는 유스케이스
 * @property initialProjectId 상세 화면에서 전달받은 초기 프로젝트 필터
 */
class BookmarkViewModel(
    private val getBookmarkedQuestions: GetBookmarkedQuestionsUseCase,
    private val bookmarkQuestion: BookmarkQuestionUseCase,
    initialProjectId: String? = null,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(BookmarkUiState(selectedFilterId = BOOKMARK_FILTER_ALL_ID))

    /**
     * 저장한 문제 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<BookmarkSideEffect>(extraBufferCapacity = 1)

    /**
     * 저장한 문제 화면에서 한 번만 처리할 이벤트 스트림이다.
     */
    val sideEffects: SharedFlow<BookmarkSideEffect> = _sideEffects.asSharedFlow()

    init {
        loadBookmarks(initialProjectId ?: BOOKMARK_FILTER_ALL_ID)
    }

    /**
     * 저장한 문제 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 저장한 문제 화면 의도
     */
    fun onIntent(intent: BookmarkIntent) {
        when (intent) {
            BookmarkIntent.HomeTabClick -> emit(BookmarkSideEffect.NavigateToHome)
            BookmarkIntent.ProjectTabClick -> emit(BookmarkSideEffect.NavigateToProjectList)
            BookmarkIntent.SavedTabClick -> Unit
            BookmarkIntent.MyTabClick -> emit(BookmarkSideEffect.NavigateToMy)
            is BookmarkIntent.FilterClick -> loadBookmarks(intent.filterId)
            is BookmarkIntent.BookmarkClick -> toggleBookmark(intent.questionId)
            is BookmarkIntent.SolveClick -> {
                // TODO: 문제풀이 화면 route 추가 후 연결한다.
            }
        }
    }

    private fun emit(sideEffect: BookmarkSideEffect) {
        viewModelScope.launch { _sideEffects.emit(sideEffect) }
    }

    private fun setState(reducer: BookmarkUiState.() -> BookmarkUiState) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * 문제의 북마크 상태를 반전해 서버에 설정하고, 적용된 값을 화면에 반영한다.
     * 실패하면 표시 상태를 유지하고 원인을 로그로 남긴다.
     *
     * @param questionId 북마크를 전환할 문제 식별자
     */
    private fun toggleBookmark(questionId: String) {
        val state = _uiState.value
        val question = state.questions.firstOrNull { it.id == questionId } ?: return
        val desired = !(state.bookmarkChanges[questionId] ?: true)

        viewModelScope.launch {
            bookmarkQuestion(question.projectId, questionId, desired)
                .onSuccess { applied ->
                    setState { copy(bookmarkChanges = bookmarkChanges + (questionId to applied)) }
                }.onFailure { error -> logger.e(throwable = error) { "문제 북마크 변경 실패" } }
        }
    }

    /**
     * 선택한 필터로 북마크 목록을 조회해 화면 상태를 채운다.
     * 실패하면 이전 목록을 유지하고 원인을 로그로 남긴다.
     *
     * @param filterId 조회할 프로젝트 필터 식별자. 전체면 프로젝트 조건 없이 조회한다
     */
    private fun loadBookmarks(filterId: String) {
        viewModelScope.launch {
            getBookmarkedQuestions(projectId = filterId.takeIf { it != BOOKMARK_FILTER_ALL_ID })
                .onSuccess { bookmarks -> _uiState.value = bookmarks.toUiState(selectedFilterId = filterId) }
                .onFailure { error -> logger.e(throwable = error) { "북마크 목록 조회 실패" } }
        }
    }
}
