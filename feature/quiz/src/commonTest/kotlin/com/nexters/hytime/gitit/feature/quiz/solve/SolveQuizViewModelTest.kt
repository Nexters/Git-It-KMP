package com.nexters.hytime.gitit.feature.quiz.solve

import com.nexters.hytime.gitit.designsystem.quiz.GitItMultipleChoiceAnswerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 문제 풀이 ViewModel의 핵심 상태 전환을 검증한다. */
class SolveQuizViewModelTest {
    /** 시작·선택·정답 제출이 결과 화면에 필요한 상태를 만든다. */
    @Test
    fun onIntent_correctAnswer_expandsOnlyCorrectAnswer() {
        val viewModel = createViewModel()

        viewModel.onIntent(SolveQuizIntent.Start)
        viewModel.onIntent(SolveQuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(SolveQuizIntent.Submit)

        val state = viewModel.uiState.value
        assertTrue(state.isStarted)
        assertTrue(state.isSubmitted)
        assertEquals(setOf("set-content"), state.expandedAnswerIds)
    }

    /** 오답 제출 시 선택한 오답과 실제 정답을 함께 펼친다. */
    @Test
    fun onIntent_incorrectAnswer_expandsIncorrectAndCorrectAnswers() {
        val viewModel = createViewModel()

        viewModel.onIntent(SolveQuizIntent.AnswerClick("render"))
        viewModel.onIntent(SolveQuizIntent.Submit)

        assertEquals(setOf("render", "set-content"), viewModel.uiState.value.expandedAnswerIds)
    }

    /** 채점 후 답안과 북마크를 다시 누르면 각각 표시 상태를 전환한다. */
    @Test
    fun onIntent_resultAnswerAndBookmark_togglesStates() {
        val viewModel = createViewModel()
        viewModel.onIntent(SolveQuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(SolveQuizIntent.Submit)

        viewModel.onIntent(SolveQuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(SolveQuizIntent.BookmarkClick)

        assertFalse("set-content" in viewModel.uiState.value.expandedAnswerIds)
        assertTrue(viewModel.uiState.value.isBookmarked)
    }

    /** 오답 결과가 선택한 답안과 실제 정답에 서로 다른 디자인 상태를 적용한다. */
    @Test
    fun answerCardState_incorrectResult_mapsIncorrectAndCorrectStates() {
        val state =
            SolveQuizUiState(
                selectedAnswerId = "render",
                isSubmitted = true,
                expandedAnswerIds = setOf("render", "set-content"),
            )

        assertEquals(GitItMultipleChoiceAnswerState.Incorrect, state.answerCardState("render"))
        assertEquals(GitItMultipleChoiceAnswerState.Correct, state.answerCardState("set-content"))
        assertEquals(GitItMultipleChoiceAnswerState.Folded, state.answerCardState("set-state"))
    }

    private fun createViewModel() = SolveQuizViewModel(projectId = "project-1")
}
