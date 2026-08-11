package com.nexters.hytime.gitit.feature.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** 로컬 샘플 문제의 상태와 사용자 의도를 관리한다. */
class QuizViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())

    /** 문제 풀이 화면이 구독할 현재 상태다. */
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<QuizSideEffect>(extraBufferCapacity = 1)

    /** 화면 이동과 외부 URL 열기를 전달하는 이벤트 스트림이다. */
    val sideEffects: SharedFlow<QuizSideEffect> = _sideEffects.asSharedFlow()

    /**
     * 문제 풀이 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 문제 풀이 의도
     */
    fun onIntent(intent: QuizIntent) {
        when (intent) {
            QuizIntent.Start -> setState { copy(isStarted = true) }
            QuizIntent.BackClick -> _sideEffects.tryEmit(QuizSideEffect.NavigateBack)
            QuizIntent.Submit -> submitAnswer()
            QuizIntent.BookmarkClick -> setState { copy(isBookmarked = !isBookmarked) }
            QuizIntent.OpenSource -> _sideEffects.tryEmit(QuizSideEffect.OpenUrl(uiState.value.question.sourceUrl))
            is QuizIntent.AnswerClick -> onAnswerClick(intent.answerId)
        }
    }

    private fun onAnswerClick(answerId: String) {
        if (uiState.value.question.answers
                .none { it.id == answerId }
        ) {
            return
        }

        if (uiState.value.isSubmitted) {
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
        val selectedAnswerId = state.selectedAnswerId ?: return
        if (state.isSubmitted) return

        setState {
            copy(
                isSubmitted = true,
                expandedAnswerIds = setOf(selectedAnswerId, question.correctAnswerId),
            )
        }
    }

    private fun setState(reducer: QuizUiState.() -> QuizUiState) {
        _uiState.value = _uiState.value.reducer()
    }
}
