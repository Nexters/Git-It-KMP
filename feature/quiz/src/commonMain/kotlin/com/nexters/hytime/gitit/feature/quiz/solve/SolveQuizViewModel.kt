package com.nexters.hytime.gitit.feature.quiz.solve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.model.LearningSetSummary
import com.nexters.hytime.gitit.domain.usecase.GetLearningSetUseCase
import com.nexters.hytime.gitit.domain.usecase.GetProjectDetailUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 문제 풀이 화면 진입에 필요한 인자다.
 *
 * @property projectId 문제를 불러올 프로젝트 식별자
 * @property setId 특정 학습 세트로 제한할 때 사용하는 선택적 식별자
 */
data class SolveQuizArgs(
    val projectId: String,
    val setId: String? = null,
)

/**
 * 학습 세트의 문제들을 순서대로 풀어 나가는 상태와 사용자 의도를 관리한다.
 *
 * @property args 프로젝트·세트 식별자
 * @property getProjectDetail 세트 라벨과 이어 풀 세트를 정하기 위한 프로젝트 상세 조회 유스케이스
 * @property getLearningSet 문제 목록을 조회하는 유스케이스
 */
class SolveQuizViewModel(
    private val args: SolveQuizArgs,
    private val getProjectDetail: GetProjectDetailUseCase,
    private val getLearningSet: GetLearningSetUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(SolveQuizUiState())

    /** 문제 풀이 화면이 구독할 현재 상태다. */
    val uiState: StateFlow<SolveQuizUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<SolveQuizSideEffect>(extraBufferCapacity = 1)

    /** 화면 이동과 외부 URL 열기를 전달하는 이벤트 스트림이다. */
    val sideEffects: SharedFlow<SolveQuizSideEffect> = _sideEffects.asSharedFlow()

    init {
        loadLearningSet()
    }

    /**
     * 문제 풀이 화면에서 발생한 사용자 의도를 처리한다.
     *
     * @param intent 사용자가 발생시킨 문제 풀이 의도
     */
    fun onIntent(intent: SolveQuizIntent) {
        when (intent) {
            SolveQuizIntent.Start -> startSolving()
            SolveQuizIntent.BackClick -> {
                setState { copy(step = QuizStep.Intro, currentIndex = 0).clearQuestionState() }
                _sideEffects.tryEmit(SolveQuizSideEffect.NavigateBack)
            }
            SolveQuizIntent.Submit -> submitAnswer()
            SolveQuizIntent.Next -> moveToNextQuestion()
            SolveQuizIntent.BookmarkClick -> toggleBookmark()
            SolveQuizIntent.OpenSource -> openSource()
            is SolveQuizIntent.AnswerClick -> onAnswerClick(intent.answerId)
            is SolveQuizIntent.EssayAnswerChange -> updateEssayAnswer(intent.answer)
        }
    }

    /**
     * 프로젝트 상세로 풀 세트를 정하고 학습 세트를 조회해 문제 목록을 채운다.
     *
     * 세트 식별자가 없으면 아직 다 풀지 않은 첫 세트를, 모두 풀었다면 첫 세트를 고른다.
     * 실패하면 빈 상태를 유지하고 원인을 로그로 남긴다.
     */
    private fun loadLearningSet() {
        viewModelScope.launch {
            getProjectDetail(args.projectId)
                .onSuccess { detail ->
                    val target = detail.sets.targetSummary(args.setId)
                    if (target == null) {
                        logger.e { "풀 수 있는 학습 세트가 없습니다: projectId=${args.projectId}, setId=${args.setId}" }
                        return@onSuccess
                    }
                    getLearningSet(args.projectId, target.setId)
                        .onSuccess { learningSet ->
                            setState {
                                copy(
                                    setInfo = learningSet.toSetInfo(target.label),
                                    questions = learningSet.toQuestionItems(detail.repositoryName),
                                )
                            }
                        }.onFailure { error -> logger.e(throwable = error) { "학습 세트 조회 실패" } }
                }.onFailure { error -> logger.e(throwable = error) { "프로젝트 상세 조회 실패" } }
        }
    }

    /** 첫 문제 형식에 맞는 단계로 이동해 풀이를 시작한다. 문제가 없으면 완료 화면을 표시한다. */
    private fun startSolving() {
        setState {
            copy(
                currentIndex = 0,
                step = questions.firstOrNull()?.toStep() ?: QuizStep.Completed,
            ).clearQuestionState()
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

        // TODO: 답변 제출 API 연동 후 서버 채점 결과(정답·해설)를 반영한다.
        setState {
            copy(
                isMultipleChoiceSubmitted = true,
                expandedAnswerIds = setOf(selectedAnswerId),
            )
        }
    }

    /** 현재 문제를 마쳤으면 다음 문제로 이동하고, 마지막 문제였다면 완료 화면을 표시한다. */
    private fun moveToNextQuestion() {
        val state = uiState.value
        val finished =
            (state.step == QuizStep.MultipleChoice && state.isMultipleChoiceSubmitted) ||
                (state.step == QuizStep.Essay && state.isEssaySubmitted)
        if (!finished) return

        setState {
            val nextIndex = currentIndex + 1
            val next = questions.getOrNull(nextIndex)
            if (next == null) {
                copy(step = QuizStep.Completed).clearQuestionState()
            } else {
                copy(currentIndex = nextIndex, step = next.toStep()).clearQuestionState()
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

    /** 출처 URL이 있을 때만 외부 열기 이벤트를 발행한다. */
    private fun openSource() {
        val url = uiState.value.currentSource().url
        if (url.isNotBlank()) {
            _sideEffects.tryEmit(SolveQuizSideEffect.OpenUrl(url))
        }
    }

    private fun setState(reducer: SolveQuizUiState.() -> SolveQuizUiState) {
        _uiState.value = _uiState.value.reducer()
    }
}

/**
 * 이어서 풀 세트 요약을 고른다.
 *
 * @param setId 화면 진입 시 지정한 세트 식별자. 없으면 진행 상황으로 고른다
 * @return 지정한 세트, 없으면 다 풀지 않은 첫 세트, 그것도 없으면 첫 세트
 */
private fun List<LearningSetSummary>.targetSummary(setId: String?): LearningSetSummary? =
    if (setId != null) {
        firstOrNull { it.setId == setId }
    } else {
        firstOrNull { it.completedCount < it.problemCount } ?: firstOrNull()
    }

/** 문제 형식에 맞는 풀이 단계를 반환한다. */
private fun SolveQuizQuestionItem.toStep(): QuizStep =
    when (this) {
        is SolveQuizQuestionItem.MultipleChoice -> QuizStep.MultipleChoice
        is SolveQuizQuestionItem.Essay -> QuizStep.Essay
    }

/** 문제 하나에만 유효한 선택·제출·펼침 상태를 초기화한다. */
private fun SolveQuizUiState.clearQuestionState(): SolveQuizUiState =
    copy(
        selectedAnswerId = null,
        isMultipleChoiceSubmitted = false,
        expandedAnswerIds = emptySet(),
        essayAnswer = "",
        isEssaySubmitted = false,
    )

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
