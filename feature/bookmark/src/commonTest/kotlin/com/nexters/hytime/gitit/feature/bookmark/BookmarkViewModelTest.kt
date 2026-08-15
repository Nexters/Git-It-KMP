package com.nexters.hytime.gitit.feature.bookmark

import kotlin.test.Test
import kotlin.test.assertEquals

/** 저장한 문제 ViewModel의 북마크 상태 전환을 검증한다. */
class BookmarkViewModelTest {
    /** 저장된 문제를 반복 선택하면 해제 상태와 저장 상태를 차례로 반영한다. */
    @Test
    fun onIntent_bookmarkClick_togglesBookmarkState() {
        val viewModel = BookmarkViewModel()
        val questionId =
            viewModel.uiState.value.questions
                .first()
                .id

        assertEquals(emptyMap(), viewModel.uiState.value.bookmarkChanges)

        viewModel.onIntent(BookmarkIntent.BookmarkClick(questionId))
        assertEquals(mapOf(questionId to false), viewModel.uiState.value.bookmarkChanges)

        viewModel.onIntent(BookmarkIntent.BookmarkClick(questionId))
        assertEquals(mapOf(questionId to true), viewModel.uiState.value.bookmarkChanges)
    }
}
