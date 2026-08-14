package com.nexters.hytime.gitit.feature.quiz.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.feature.quiz.create.generation.QuizGenerationCoordinator
import com.nexters.hytime.gitit.feature.quiz.create.generation.QuizGenerationStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 문제 생성 설정과 사용자에게 보여줄 진행 상태를 관리한다.
 *
 * @param projectId 문제를 생성할 프로젝트 식별자
 * @param coordinator 화면 이동 후에도 유지되는 문제 생성 코디네이터
 */
class QuizCreateViewModel(
    projectId: String,
    private val coordinator: QuizGenerationCoordinator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizCreateUiState(projectId = projectId))

    /** 화면이 구독할 현재 생성 상태다. */
    val uiState: StateFlow<QuizCreateUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<QuizCreateEvent>(extraBufferCapacity = 1)

    /** 화면 이동과 권한 요청을 전달하는 이벤트 스트림이다. */
    val events: SharedFlow<QuizCreateEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            coordinator.state.collect { generationState ->
                if (generationState.projectId != uiState.value.projectId) return@collect
                setState {
                    copy(
                        stage =
                            when (generationState.status) {
                                QuizGenerationStatus.Idle -> stage
                                QuizGenerationStatus.Generating,
                                QuizGenerationStatus.Completing,
                                QuizGenerationStatus.Completed,
                                QuizGenerationStatus.Failed,
                                -> QuizCreateStage.Generating
                            },
                        generationStep = generationState.step,
                        progressPercent = generationState.progressPercent,
                        showReminderPrompt = false,
                    )
                }
                val isTerminal =
                    generationState.status == QuizGenerationStatus.Completed ||
                        generationState.status == QuizGenerationStatus.Failed
                if (isTerminal && !generationState.isHomeModalVisible) {
                    coordinator.showHomeModal()
                    emit(QuizCreateEvent.NavigateHome)
                }
            }
        }
    }

    /**
     * 문제 생성 화면에서 발생한 의도를 처리한다.
     *
     * @param intent 사용자가 발생시키거나 생성 서비스가 전달한 의도
     */
    fun onIntent(intent: QuizCreateIntent) {
        when (intent) {
            QuizCreateIntent.BackClick -> moveBack()
            QuizCreateIntent.NextClick -> moveNext()
            QuizCreateIntent.StartGeneration -> startGeneration()
            QuizCreateIntent.WaitAtHome -> setState { copy(showReminderPrompt = true) }
            QuizCreateIntent.EnableReminder -> navigateHome(showPermissionRequest = true)
            QuizCreateIntent.DismissReminder -> navigateHome(showPermissionRequest = false)
            is QuizCreateIntent.SelectKnowledge -> setState { copy(knowledgeLevel = intent.level) }
            is QuizCreateIntent.ToggleTopic -> toggleTopic(intent.topic)
            is QuizCreateIntent.GenerationProgressChanged ->
                setState {
                    if (stage != QuizCreateStage.Generating) {
                        this
                    } else {
                        copy(
                            generationStep = intent.step,
                            progressPercent = intent.progressPercent.coerceIn(0, 100),
                        )
                    }
                }
            QuizCreateIntent.GenerationSucceeded -> coordinator.complete(uiState.value.projectId)
            QuizCreateIntent.GenerationFailed -> coordinator.fail(uiState.value.projectId)
        }
    }

    /** 필수 선택값이 준비된 경우 다음 설정 단계로 이동한다. */
    private fun moveNext() {
        setState {
            when (stage) {
                QuizCreateStage.Knowledge if knowledgeLevel != null -> copy(stage = QuizCreateStage.Topics)
                QuizCreateStage.Topics if topics.isNotEmpty() -> copy(stage = QuizCreateStage.Ready)
                else -> this
            }
        }
    }

    /** 현재 설정 단계에서 이전 단계 또는 이전 화면으로 이동한다. */
    private fun moveBack() {
        when (uiState.value.stage) {
            QuizCreateStage.Knowledge -> emit(QuizCreateEvent.NavigateBack)
            QuizCreateStage.Topics -> setState { copy(stage = QuizCreateStage.Knowledge) }
            QuizCreateStage.Ready -> setState { copy(stage = QuizCreateStage.Topics) }
            QuizCreateStage.Generating -> Unit
        }
    }

    /** 준비 단계에서 새 문제 생성 세션을 시작한다. */
    private fun startGeneration() {
        if (uiState.value.stage != QuizCreateStage.Ready) return

        setState {
            copy(
                stage = QuizCreateStage.Generating,
                generationStep = QuizGenerationStep.ProjectInfo,
                progressPercent = 0,
                showReminderPrompt = false,
            )
        }
        coordinator.start(uiState.value.projectId)
    }

    /**
     * 홈 진행 모달을 활성화하고 홈 이동 이벤트를 전달한다.
     *
     * @param showPermissionRequest 홈 이동 전에 알림 권한 요청 이벤트를 함께 전달할지 여부
     */
    private fun navigateHome(showPermissionRequest: Boolean) {
        coordinator.showHomeModal()
        emit(
            if (showPermissionRequest) {
                QuizCreateEvent.EnableReminderAndNavigateHome
            } else {
                QuizCreateEvent.NavigateHome
            },
        )
    }

    /**
     * 전달받은 주제의 선택 여부를 반전한다.
     *
     * @param topic 선택 상태를 변경할 문제 주제
     */
    private fun toggleTopic(topic: QuizCreateTopic) {
        setState {
            copy(
                topics =
                    if (topic in topics) {
                        topics - topic
                    } else {
                        topics + topic
                    },
            )
        }
    }

    /**
     * 현재 UI 상태에 리듀서를 적용한다.
     *
     * @param reducer 기존 상태를 기반으로 새 상태를 만드는 함수
     */
    private fun setState(reducer: QuizCreateUiState.() -> QuizCreateUiState) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * 화면에서 한 번만 처리할 이벤트를 전달한다.
     *
     * @param event 내비게이션이나 권한 요청에 사용할 이벤트
     */
    private fun emit(event: QuizCreateEvent) {
        _events.tryEmit(event)
    }
}
