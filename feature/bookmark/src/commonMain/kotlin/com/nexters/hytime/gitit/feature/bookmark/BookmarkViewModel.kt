package com.nexters.hytime.gitit.feature.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 저장한 문제 화면의 상태와 사용자 의도를 관리한다.
 */
class BookmarkViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(dummyBookmarkUiState)

    /**
     * 저장한 문제 화면이 구독할 현재 UI 상태이다.
     */
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<BookmarkSideEffect>(extraBufferCapacity = 1)

    /**
     * 저장한 문제 화면에서 한 번만 처리할 이벤트 스트림이다.
     */
    val sideEffects: SharedFlow<BookmarkSideEffect> = _sideEffects.asSharedFlow()

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
            is BookmarkIntent.FilterClick -> {
                _uiState.update { state -> state.copy(selectedFilterId = intent.filterId) }
            }
            is BookmarkIntent.BookmarkClick -> {
                // TODO: 저장한 문제 해제 API 연동 후 상태를 갱신한다.
            }
            is BookmarkIntent.ExplanationClick -> {
                // TODO: 해설 화면 route 추가 후 연결한다.
            }
            is BookmarkIntent.SolveClick -> {
                // TODO: 문제풀이 화면 route 추가 후 연결한다.
            }
        }
    }

    private fun emit(sideEffect: BookmarkSideEffect) {
        viewModelScope.launch { _sideEffects.emit(sideEffect) }
    }
}

/** domain/data 연동 전까지 화면에 표시할 더미 저장 문제 상태다. */
private val dummyBookmarkUiState =
    BookmarkUiState(
        filters =
            listOf(
                BookmarkFilter(id = "all", label = "전체"),
                BookmarkFilter(id = "flask", label = "Flask"),
                BookmarkFilter(id = "android", label = "Now in Android"),
            ),
        selectedFilterId = "all",
        questions =
            List(4) { index ->
                BookmarkedQuestion(
                    id = "bookmark-$index",
                    meta = "Android · Set2 · 문제 1",
                    title = "sansio/blueprints.py에 정의된 BlueprintSetupState 클래스는 어떤 목적을 가진 개체인가?",
                )
            },
    )
