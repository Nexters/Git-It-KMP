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
            SolveQuizIntent.Start -> setState { copy(isStarted = true) }
            SolveQuizIntent.BackClick -> _sideEffects.tryEmit(SolveQuizSideEffect.NavigateBack)
            SolveQuizIntent.Submit -> submitAnswer()
            SolveQuizIntent.BookmarkClick -> setState { copy(isBookmarked = !isBookmarked) }
            SolveQuizIntent.OpenSource -> _sideEffects.tryEmit(SolveQuizSideEffect.OpenUrl(uiState.value.question.sourceUrl))
            is SolveQuizIntent.AnswerClick -> onAnswerClick(intent.answerId)
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

    private fun setState(reducer: SolveQuizUiState.() -> SolveQuizUiState) {
        _uiState.value = _uiState.value.reducer()
    }
}
