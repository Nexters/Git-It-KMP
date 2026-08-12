package com.nexters.hytime.gitit.feature.quiz

import com.nexters.hytime.gitit.designsystem.quiz.GitItMultipleChoiceAnswerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 문제 풀이 ViewModel의 핵심 상태 전환을 검증한다. */
class QuizViewModelTest {
    /** 시작·선택·정답 제출이 결과 화면에 필요한 상태를 만든다. */
    @Test
    fun onIntent_correctAnswer_expandsOnlyCorrectAnswer() {
        val viewModel = QuizViewModel()

        viewModel.onIntent(QuizIntent.Start)
        viewModel.onIntent(QuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(QuizIntent.Submit)

        val state = viewModel.uiState.value
        assertEquals(QuizStep.MultipleChoice, state.step)
        assertTrue(state.isMultipleChoiceSubmitted)
        assertEquals(setOf("set-content"), state.expandedAnswerIds)
    }

    /** 오답 제출 시 선택한 오답과 실제 정답을 함께 펼친다. */
    @Test
    fun onIntent_incorrectAnswer_expandsIncorrectAndCorrectAnswers() {
        val viewModel = QuizViewModel()

        viewModel.onIntent(QuizIntent.Start)
        viewModel.onIntent(QuizIntent.AnswerClick("render"))
        viewModel.onIntent(QuizIntent.Submit)

        assertEquals(setOf("render", "set-content"), viewModel.uiState.value.expandedAnswerIds)
    }

    /** 채점 후 답안과 북마크를 다시 누르면 각각 표시 상태를 전환한다. */
    @Test
    fun onIntent_resultAnswerAndBookmark_togglesStates() {
        val viewModel = QuizViewModel()
        viewModel.onIntent(QuizIntent.Start)
        viewModel.onIntent(QuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(QuizIntent.Submit)

        viewModel.onIntent(QuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(QuizIntent.BookmarkClick)

        assertFalse("set-content" in viewModel.uiState.value.expandedAnswerIds)
        assertEquals(setOf(1), viewModel.uiState.value.bookmarkedQuestionNumbers)
    }

    /** 오답 결과가 선택한 답안과 실제 정답에 서로 다른 디자인 상태를 적용한다. */
    @Test
    fun answerCardState_incorrectResult_mapsIncorrectAndCorrectStates() {
        val state =
            QuizUiState(
                selectedAnswerId = "render",
                isMultipleChoiceSubmitted = true,
                expandedAnswerIds = setOf("render", "set-content"),
            )

        assertEquals(GitItMultipleChoiceAnswerState.Incorrect, state.answerCardState("render"))
        assertEquals(GitItMultipleChoiceAnswerState.Correct, state.answerCardState("set-content"))
        assertEquals(GitItMultipleChoiceAnswerState.Folded, state.answerCardState("set-state"))
    }

    /** 객관식 채점 후 다음을 누르면 서술형 문제로 이동한다. */
    @Test
    fun onIntent_multipleChoiceNext_movesToEssay() {
        val viewModel = QuizViewModel()
        viewModel.onIntent(QuizIntent.Start)
        viewModel.onIntent(QuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(QuizIntent.Submit)

        viewModel.onIntent(QuizIntent.Next)

        assertEquals(QuizStep.Essay, viewModel.uiState.value.step)
    }

    /** 서술형 답안은 최대 글자 수까지만 저장한다. */
    @Test
    fun onIntent_essayAnswerOverLimit_truncatesToMaximumLength() {
        val viewModel = essayViewModel()

        viewModel.onIntent(QuizIntent.EssayAnswerChange("가".repeat(ESSAY_ANSWER_MAX_LENGTH + 1)))

        assertEquals(ESSAY_ANSWER_MAX_LENGTH, viewModel.uiState.value.essayAnswer.length)
    }

    /** 서술형 답안을 비워도 제출하고 완료 단계로 이동할 수 있다. */
    @Test
    fun onIntent_emptyEssay_submitAndNext_movesToCompleted() {
        val viewModel = essayViewModel()

        viewModel.onIntent(QuizIntent.Submit)
        assertTrue(viewModel.uiState.value.isEssaySubmitted)

        viewModel.onIntent(QuizIntent.Next)
        assertEquals(QuizStep.Completed, viewModel.uiState.value.step)
    }

    /** 객관식과 서술형 북마크는 문제 번호별로 독립적으로 유지된다. */
    @Test
    fun onIntent_bookmarkEachQuestion_keepsBothQuestionNumbers() {
        val viewModel = QuizViewModel()
        viewModel.onIntent(QuizIntent.Start)
        viewModel.onIntent(QuizIntent.BookmarkClick)
        viewModel.onIntent(QuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(QuizIntent.Submit)
        viewModel.onIntent(QuizIntent.Next)

        viewModel.onIntent(QuizIntent.BookmarkClick)

        assertEquals(setOf(1, 2), viewModel.uiState.value.bookmarkedQuestionNumbers)
    }

    /** 서술형 단계까지 이동한 ViewModel을 만든다. */
    private fun essayViewModel(): QuizViewModel =
        QuizViewModel().apply {
            onIntent(QuizIntent.Start)
            onIntent(QuizIntent.AnswerClick("set-content"))
            onIntent(QuizIntent.Submit)
            onIntent(QuizIntent.Next)
        }
}
