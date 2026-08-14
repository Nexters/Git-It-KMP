package com.nexters.hytime.gitit.feature.quiz.solve

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 로컬 샘플 문제의 상태와 사용자 의도를 관리한다.
 *
 * @property projectId 추후 문제 조회에 사용할 프로젝트 식별자
 * @property setId 추후 특정 학습 세트 조회에 사용할 선택적 식별자
 */
class SolveQuizViewModel(
    val projectId: String,
    val setId: String? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SolveQuizUiState())

    /** 문제 풀이 화면이 구독할 현재 상태다. */
    val uiState: StateFlow<SolveQuizUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<SolveQuizSideEffect>(extraBufferCapacity = 1)

    /** 화면 이동과 외부 URL 열기를 전달하는 이벤트 스트림이다. */
    val sideEffects: SharedFlow<SolveQuizSideEffect> = _sideEffects.asSharedFlow()

    /**
     * 문제 풀이 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 문제 풀이 의도
     */
    fun onIntent(intent: SolveQuizIntent) {
        when (intent) {
            SolveQuizIntent.Start -> setState { copy(step = QuizStep.MultipleChoice) }
            SolveQuizIntent.BackClick -> _sideEffects.tryEmit(SolveQuizSideEffect.NavigateBack)
            SolveQuizIntent.Submit -> submitAnswer()
            SolveQuizIntent.Next -> moveToNextStep()
            SolveQuizIntent.BookmarkClick -> toggleBookmark()
            SolveQuizIntent.OpenSource -> _sideEffects.tryEmit(SolveQuizSideEffect.OpenUrl(uiState.value.currentSource().url))
            is SolveQuizIntent.AnswerClick -> onAnswerClick(intent.answerId)
            is SolveQuizIntent.EssayAnswerChange -> updateEssayAnswer(intent.answer)
        }
    }

    private fun onAnswerClick(answerId: String) {
        if (uiState.value.multipleChoiceQuestion.answers
                .none { it.id == answerId }
        ) {
            return
        }

        if (uiState.value.isMultipleChoiceSubmitted) {
            setState {
                copy(
                    expandedAnswerIds =
                        if (answerId in expandedAnswerIds) {
                            expandedAnswerIds - answerId
                        } else {
                            expandedAnswerIds + answerId
                        },
                )
            }
        } else {
            setState { copy(selectedAnswerId = answerId) }
        }
    }

    private fun submitAnswer() {
        val state = uiState.value
        when (state.step) {
            QuizStep.MultipleChoice -> submitMultipleChoiceAnswer(state)
            QuizStep.Essay -> setState { copy(isEssaySubmitted = true) }
            QuizStep.Intro,
            QuizStep.Completed,
            -> Unit
        }
    }

    private fun submitMultipleChoiceAnswer(state: SolveQuizUiState) {
        val selectedAnswerId = state.selectedAnswerId ?: return
        if (state.isMultipleChoiceSubmitted) return

        setState {
            copy(
                isMultipleChoiceSubmitted = true,
                expandedAnswerIds = setOf(selectedAnswerId, multipleChoiceQuestion.correctAnswerId),
            )
        }
    }

    private fun moveToNextStep() {
        setState {
            when {
                step == QuizStep.MultipleChoice && isMultipleChoiceSubmitted -> copy(step = QuizStep.Essay)
                step == QuizStep.Essay && isEssaySubmitted -> copy(step = QuizStep.Completed)
                else -> this
            }
        }
    }

    private fun updateEssayAnswer(answer: String) {
        if (uiState.value.step != QuizStep.Essay || uiState.value.isEssaySubmitted) return
        setState { copy(essayAnswer = answer.take(ESSAY_ANSWER_MAX_LENGTH)) }
    }

    private fun toggleBookmark() {
        val questionNumber = uiState.value.currentQuestionNumber()
        setState {
            copy(
                bookmarkedQuestionNumbers =
                    if (questionNumber in bookmarkedQuestionNumbers) {
                        bookmarkedQuestionNumbers - questionNumber
                    } else {
                        bookmarkedQuestionNumbers + questionNumber
                    },
            )
        }
    }

    private fun setState(reducer: SolveQuizUiState.() -> SolveQuizUiState) {
        _uiState.value = _uiState.value.reducer()
    }
}

/** 현재 단계의 문제 번호를 반환한다. */
private fun SolveQuizUiState.currentQuestionNumber(): Int =
    if (step ==
        QuizStep.Essay
    ) {
        essayQuestion.number
    } else {
        multipleChoiceQuestion.number
    }

/** 현재 단계의 문제 출처를 반환한다. */
private fun SolveQuizUiState.currentSource(): QuizSource =
    if (step ==
        QuizStep.Essay
    ) {
        essayQuestion.source
    } else {
        multipleChoiceQuestion.source
    }
