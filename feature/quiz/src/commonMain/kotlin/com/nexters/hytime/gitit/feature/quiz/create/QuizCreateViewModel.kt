package com.nexters.hytime.gitit.feature.quiz.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.usecase.RegisterProjectUseCase
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateRequest
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStatus
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStore
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
 * @param repositoryUrl 프로젝트로 등록할 GitHub 저장소 URL
 * @param registerProject 학습 설정으로 서버 프로젝트를 등록하는 UseCase
 * @param createStore 화면 이동 후에도 유지되는 문제 생성 세션 Store
 */
class QuizCreateViewModel(
    repositoryUrl: String,
    private val registerProject: RegisterProjectUseCase,
    private val createStore: QuizCreateStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizCreateUiState(repositoryUrl = repositoryUrl))

    /** 화면이 구독할 현재 생성 상태다. */
    val uiState: StateFlow<QuizCreateUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<QuizCreateEvent>(extraBufferCapacity = 1)

    /** 화면 이동과 권한 요청을 전달하는 이벤트 스트림이다. */
    val events: SharedFlow<QuizCreateEvent> = _events.asSharedFlow()

    /** 현재 화면 흐름에서 홈 이동 이벤트를 이미 전달했는지 여부다. */
    private var hasNavigatedHome = false

    init {
        viewModelScope.launch {
            createStore.state.collect { createState ->
                if (createState.projectId != uiState.value.projectId) return@collect
                setState {
                    copy(
                        stage =
                            when (createState.status) {
                                QuizCreateStatus.Idle -> stage
                                QuizCreateStatus.InProgress,
                                QuizCreateStatus.Completing,
                                QuizCreateStatus.Completed,
                                QuizCreateStatus.Failed,
                                -> QuizCreateStage.Create
                            },
                        createStep = createState.step,
                        progressPercent = createState.progressPercent,
                    )
                }
                val isTerminal =
                    createState.status == QuizCreateStatus.Completed ||
                        createState.status == QuizCreateStatus.Failed
                if (isTerminal && !hasNavigatedHome) {
                    navigateHome(showPermissionRequest = false)
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
            QuizCreateIntent.StartCreate -> startCreate()
            QuizCreateIntent.WaitAtHome -> setState { copy(showReminderPrompt = true) }
            QuizCreateIntent.EnableReminder -> navigateHome(showPermissionRequest = true)
            QuizCreateIntent.DismissReminder -> navigateHome(showPermissionRequest = false)
            QuizCreateIntent.CloseReminder -> setState { copy(showReminderPrompt = false) }
            is QuizCreateIntent.SelectKnowledge -> setState { copy(knowledgeLevel = intent.level) }
            is QuizCreateIntent.CreateProgressChanged ->
                setState {
                    if (stage != QuizCreateStage.Create) {
                        this
                    } else {
                        copy(
                            createStep = intent.step,
                            progressPercent = intent.progressPercent.coerceIn(0, 100),
                        )
                    }
                }
            QuizCreateIntent.CreateSucceeded -> updateCreateResult(createStore::complete)
            QuizCreateIntent.CreateFailed -> updateCreateResult(createStore::fail)
        }
    }

    /** 필수 선택값이 준비된 경우 다음 설정 단계로 이동한다. */
    private fun moveNext() {
        setState {
            when (stage) {
                QuizCreateStage.Knowledge if knowledgeLevel != null -> copy(stage = QuizCreateStage.Ready)
                else -> this
            }
        }
    }

    /** 현재 설정 단계에서 이전 단계 또는 이전 화면으로 이동한다. */
    private fun moveBack() {
        when (uiState.value.stage) {
            QuizCreateStage.Knowledge -> emit(QuizCreateEvent.NavigateBack)
            QuizCreateStage.Ready -> setState { copy(stage = QuizCreateStage.Knowledge) }
            QuizCreateStage.Create -> Unit
        }
    }

    /** 준비 단계에서 새 문제 생성 세션을 시작한다. */
    private fun startCreate() {
        val state = uiState.value
        val knowledgeLevel = state.knowledgeLevel ?: return
        if (state.stage != QuizCreateStage.Ready || state.isRegistering) return

        val request =
            QuizCreateRequest(
                repositoryUrl = state.repositoryUrl,
                quizLevel = knowledgeLevel.toProjectQuizLevel(),
            )
        setState { copy(isRegistering = true) }
        viewModelScope.launch {
            val registration =
                registerProject(
                    githubRepoUrl = request.repositoryUrl,
                    quizLevel = request.quizLevel,
                ).getOrElse {
                    setState { copy(isRegistering = false) }
                    return@launch
                }
            startRegisteredProject(registration, request)
        }
    }

    /**
     * 서버가 발급한 프로젝트 ID로 표시용 진행 세션을 시작한다.
     *
     * @param registration 프로젝트 ID와 등록 시점의 서버 생성 상태
     * @param request 실패 시 동일 조건으로 프로젝트 등록 API를 다시 호출하기 위한 입력값
     */
    private suspend fun startRegisteredProject(
        registration: ProjectRegistration,
        request: QuizCreateRequest,
    ) {
        hasNavigatedHome = false
        setState {
            copy(
                projectId = registration.projectId,
                stage = QuizCreateStage.Create,
                createStep = QuizCreateStep.ProjectInfo,
                progressPercent = 0,
                showReminderPrompt = false,
                isRegistering = false,
            )
        }
        createStore.start(registration, request)
    }

    /**
     * 리마인드 안내를 닫고 홈 이동 이벤트를 전달한다.
     *
     * @param showPermissionRequest 홈 이동 전에 알림 권한 요청 이벤트를 함께 전달할지 여부
     */
    private fun navigateHome(showPermissionRequest: Boolean) {
        hasNavigatedHome = true
        setState { copy(showReminderPrompt = false) }
        emit(
            if (showPermissionRequest) {
                QuizCreateEvent.EnableReminderAndNavigateHome
            } else {
                QuizCreateEvent.NavigateHome
            },
        )
    }

    /**
     * 현재 프로젝트가 있으면 앱 범위 생성 상태에 서버 결과를 전달한다.
     *
     * @param update 완료 또는 실패 상태를 Store에 반영하는 동작
     */
    private fun updateCreateResult(update: suspend (String) -> Unit) {
        val projectId = uiState.value.projectId ?: return
        viewModelScope.launch { update(projectId) }
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

/**
 * 화면의 이해도 선택을 서버가 요구하는 문제 학습 깊이로 변환한다.
 *
 * @return 낮은 이해도부터 높은 이해도까지 순서대로 L1~L3에 대응한 값
 */
private fun QuizKnowledgeLevel.toProjectQuizLevel(): ProjectQuizLevel =
    when (this) {
        QuizKnowledgeLevel.Concepts -> ProjectQuizLevel.L1
        QuizKnowledgeLevel.SomeCode -> ProjectQuizLevel.L2
        QuizKnowledgeLevel.Experienced -> ProjectQuizLevel.L3
    }
