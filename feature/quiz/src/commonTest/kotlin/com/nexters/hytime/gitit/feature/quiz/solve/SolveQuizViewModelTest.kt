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
        assertEquals(QuizStep.MultipleChoice, state.step)
        assertTrue(state.isMultipleChoiceSubmitted)
        assertEquals(setOf("set-content"), state.expandedAnswerIds)
    }

    /** 오답 제출 시 선택한 오답과 실제 정답을 함께 펼친다. */
    @Test
    fun onIntent_incorrectAnswer_expandsIncorrectAndCorrectAnswers() {
        val viewModel = createViewModel()

        viewModel.onIntent(SolveQuizIntent.Start)
        viewModel.onIntent(SolveQuizIntent.AnswerClick("render"))
        viewModel.onIntent(SolveQuizIntent.Submit)

        assertEquals(setOf("render", "set-content"), viewModel.uiState.value.expandedAnswerIds)
    }

    /** 채점 후 답안과 북마크를 다시 누르면 각각 표시 상태를 전환한다. */
    @Test
    fun onIntent_resultAnswerAndBookmark_togglesStates() {
        val viewModel = createViewModel()
        viewModel.onIntent(SolveQuizIntent.Start)
        viewModel.onIntent(SolveQuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(SolveQuizIntent.Submit)

        viewModel.onIntent(SolveQuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(SolveQuizIntent.BookmarkClick)

        assertFalse("set-content" in viewModel.uiState.value.expandedAnswerIds)
        assertEquals(setOf(1), viewModel.uiState.value.bookmarkedQuestionNumbers)
    }

    /** 오답 결과가 선택한 답안과 실제 정답에 서로 다른 디자인 상태를 적용한다. */
    @Test
    fun answerCardState_incorrectResult_mapsIncorrectAndCorrectStates() {
        val state =
            SolveQuizUiState(
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
        val viewModel = createViewModel()
        viewModel.onIntent(SolveQuizIntent.Start)
        viewModel.onIntent(SolveQuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(SolveQuizIntent.Submit)

        viewModel.onIntent(SolveQuizIntent.Next)

        assertEquals(QuizStep.Essay, viewModel.uiState.value.step)
    }

    /** 서술형 답안은 최대 글자 수까지만 저장한다. */
    @Test
    fun onIntent_essayAnswerOverLimit_truncatesToMaximumLength() {
        val viewModel = essayViewModel()

        viewModel.onIntent(SolveQuizIntent.EssayAnswerChange("가".repeat(ESSAY_ANSWER_MAX_LENGTH + 1)))

        assertEquals(ESSAY_ANSWER_MAX_LENGTH, viewModel.uiState.value.essayAnswer.length)
    }

    /** 서술형 답안을 비워도 제출하고 완료 단계로 이동할 수 있다. */
    @Test
    fun onIntent_emptyEssay_submitAndNext_movesToCompleted() {
        val viewModel = essayViewModel()

        viewModel.onIntent(SolveQuizIntent.Submit)
        assertTrue(viewModel.uiState.value.isEssaySubmitted)

        viewModel.onIntent(SolveQuizIntent.Next)
        assertEquals(QuizStep.Completed, viewModel.uiState.value.step)
    }

    /** 객관식과 서술형 북마크는 문제 번호별로 독립적으로 유지된다. */
    @Test
    fun onIntent_bookmarkEachQuestion_keepsBothQuestionNumbers() {
        val viewModel = createViewModel()
        viewModel.onIntent(SolveQuizIntent.Start)
        viewModel.onIntent(SolveQuizIntent.BookmarkClick)
        viewModel.onIntent(SolveQuizIntent.AnswerClick("set-content"))
        viewModel.onIntent(SolveQuizIntent.Submit)
        viewModel.onIntent(SolveQuizIntent.Next)

        viewModel.onIntent(SolveQuizIntent.BookmarkClick)

        assertEquals(setOf(1, 2), viewModel.uiState.value.bookmarkedQuestionNumbers)
    }

    /** 학습 완료 화면을 닫으면 다음 진입을 위해 초기 상태로 돌아간다. */
    @Test
    fun onIntent_completedBackClick_resetsState() {
        val viewModel = essayViewModel()
        viewModel.onIntent(SolveQuizIntent.Submit)
        viewModel.onIntent(SolveQuizIntent.Next)

        viewModel.onIntent(SolveQuizIntent.BackClick)

        assertEquals(SolveQuizUiState(), viewModel.uiState.value)
    }

    /** 서술형 단계까지 이동한 ViewModel을 만든다. */
    private fun essayViewModel(): SolveQuizViewModel =
        createViewModel().apply {
            onIntent(SolveQuizIntent.Start)
            onIntent(SolveQuizIntent.AnswerClick("set-content"))
            onIntent(SolveQuizIntent.Submit)
            onIntent(SolveQuizIntent.Next)
        }

    /** 테스트에서 사용할 프로젝트 식별자가 포함된 ViewModel을 만든다. */
    private fun createViewModel(): SolveQuizViewModel = SolveQuizViewModel(projectId = "project-1")
}
