package com.nexters.hytime.gitit.feature.quiz.create

import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.model.EssayAnswerResult
import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.domain.usecase.RegisterProjectUseCase
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStatus
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 문제 생성 설정과 진행 상태 전환을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizCreateViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    /** ViewModel의 Main dispatcher를 테스트 dispatcher로 교체한다. */
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    /** 테스트 이후 Main dispatcher를 복원한다. */
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** 이해도를 선택하면 준비 단계로 바로 이동한다. */
    @Test
    fun onIntent_knowledgeSelected_movesToReadyStage() {
        val viewModel = createViewModel()

        viewModel.onIntent(QuizCreateIntent.NextClick)
        assertEquals(QuizCreateStage.Knowledge, viewModel.uiState.value.stage)

        viewModel.onIntent(QuizCreateIntent.SelectKnowledge(QuizKnowledgeLevel.SomeCode))
        viewModel.onIntent(QuizCreateIntent.NextClick)

        assertEquals(QuizCreateStage.Ready, viewModel.uiState.value.stage)
    }

    /** 준비 단계에서 뒤로 가면 이해도 선택 단계로 돌아간다. */
    @Test
    fun onIntent_readyBackClick_returnsToKnowledgeStage() {
        val viewModel = createViewModel()

        viewModel.onIntent(QuizCreateIntent.SelectKnowledge(QuizKnowledgeLevel.SomeCode))
        viewModel.onIntent(QuizCreateIntent.NextClick)
        viewModel.onIntent(QuizCreateIntent.BackClick)

        assertEquals(QuizCreateStage.Knowledge, viewModel.uiState.value.stage)
    }

    /** 생성 진행률은 유효 범위로 보정된다. */
    @Test
    fun onIntent_createProgress_clampsAndCompletes() =
        runTest(dispatcher) {
            val store = createStore(backgroundScope)
            val viewModel = createReadyViewModel(store = store)

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()
            viewModel.onIntent(
                QuizCreateIntent.CreateProgressChanged(
                    step = QuizCreateStep.Questions,
                    progressPercent = 130,
                ),
            )

            assertEquals(QuizCreateStage.Create, viewModel.uiState.value.stage)
            assertEquals(QuizCreateStep.Questions, viewModel.uiState.value.createStep)
            assertEquals(100, viewModel.uiState.value.progressPercent)
            store.cancel()
        }

    /** 생성 실패 신호는 앱 범위 Store의 실패 상태로 전달된다. */
    @Test
    fun onIntent_createFailure_updatesStoreFailure() =
        runTest(dispatcher) {
            val store = createStore(backgroundScope)
            val viewModel = createReadyViewModel(store = store)
            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()
            viewModel.onIntent(QuizCreateIntent.CreateFailed)
            runCurrent()

            assertEquals(QuizCreateStatus.Failed, store.state.value.status)
            assertEquals(QuizCreateStage.Create, viewModel.uiState.value.stage)
            store.cancel()
        }

    /** FCM 완료 신호로 세션이 끝나면 홈 모달을 열고 홈 이동 이벤트를 전달한다. */
    @Test
    fun startCreate_sessionCompletes_movesToCompletedStage() =
        runTest(dispatcher) {
            var nowMillis = 1_000L
            val store = createStore(scope = backgroundScope, nowMillis = { nowMillis })
            val viewModel = createReadyViewModel(store)
            val event = backgroundScope.async { viewModel.events.first() }
            runCurrent()

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()
            store.complete("project-127")
            nowMillis += 1_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals(QuizCreateStatus.Completed, store.state.value.status)
            assertEquals(100, viewModel.uiState.value.progressPercent)
            assertEquals(QuizCreateEvent.NavigateHome, event.await())
            store.cancel()
        }

    /** 실패 상태를 전달하면 홈 실패 모달과 홈 이동 이벤트로 연결된다. */
    @Test
    fun onIntent_createFailure_opensHomeFailureModal() =
        runTest(dispatcher) {
            val store = createStore(backgroundScope)
            val viewModel = createReadyViewModel(store)
            val event = backgroundScope.async { viewModel.events.first() }
            runCurrent()

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()
            viewModel.onIntent(QuizCreateIntent.CreateFailed)
            runCurrent()

            assertEquals(QuizCreateStatus.Failed, store.state.value.status)
            assertEquals(QuizCreateEvent.NavigateHome, event.await())
            store.cancel()
        }

    /** 리마인드 안내를 연 뒤 진행률이 갱신되어도 안내가 닫히지 않는다. */
    @Test
    fun onIntent_waitAtHomeWithProgressUpdate_keepsReminderPrompt() =
        runTest(dispatcher) {
            var nowMillis = 1_000L
            val store = createStore(scope = backgroundScope, nowMillis = { nowMillis })
            val viewModel = createReadyViewModel(store)

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()
            viewModel.onIntent(QuizCreateIntent.WaitAtHome)
            nowMillis += 5_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals(2, viewModel.uiState.value.progressPercent)
            assertTrue(viewModel.uiState.value.showReminderPrompt)
            store.cancel()
        }

    /** 버튼 없이 시트를 내리면 안내만 닫고 생성 화면에 머무른다. */
    @Test
    fun onIntent_closeReminder_hidesPromptWithoutNavigation() =
        runTest(dispatcher) {
            val store = createStore(backgroundScope)
            val viewModel = createReadyViewModel(store)
            val events = mutableListOf<QuizCreateEvent>()
            backgroundScope.launch { viewModel.events.toList(events) }
            runCurrent()

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()
            viewModel.onIntent(QuizCreateIntent.WaitAtHome)
            viewModel.onIntent(QuizCreateIntent.CloseReminder)
            runCurrent()

            assertFalse(viewModel.uiState.value.showReminderPrompt)
            assertEquals(QuizCreateStage.Create, viewModel.uiState.value.stage)
            assertTrue(events.isEmpty())
            store.cancel()
        }

    /** 리마인드 안내가 열린 채로 생성이 끝나면 안내를 닫고 홈으로 이동한다. */
    @Test
    fun startCreate_completesWhileReminderOpen_closesPromptAndNavigatesHome() =
        runTest(dispatcher) {
            var nowMillis = 1_000L
            val store = createStore(scope = backgroundScope, nowMillis = { nowMillis })
            val viewModel = createReadyViewModel(store)
            val event = backgroundScope.async { viewModel.events.first() }
            runCurrent()

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()
            viewModel.onIntent(QuizCreateIntent.WaitAtHome)
            store.complete("project-127")
            nowMillis += 1_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertFalse(viewModel.uiState.value.showReminderPrompt)
            assertEquals(QuizCreateEvent.NavigateHome, event.await())
            store.cancel()
        }

    /** 선택한 이해도를 서버 난이도로 변환하고 반환된 프로젝트 ID로 생성을 시작한다. */
    @Test
    fun startCreate_registrationSucceeds_usesApiProjectId() =
        runTest(dispatcher) {
            val repository = RecordingProjectRepository()
            val store = createStore(backgroundScope)
            val viewModel = createReadyViewModel(store = store, projectRepository = repository)

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()

            assertEquals(REPOSITORY_URL, repository.githubRepoUrl)
            assertEquals(ProjectQuizLevel.L1, repository.quizLevel)
            assertEquals("project-127", viewModel.uiState.value.projectId)
            assertEquals("project-127", store.state.value.projectId)
            assertEquals(QuizCreateStage.Create, viewModel.uiState.value.stage)
            store.cancel()
        }

    /** 프로젝트 등록이 실패하면 진행 세션을 시작하지 않고 시작 버튼을 다시 활성화한다. */
    @Test
    fun startCreate_registrationFails_staysReadyForRetry() =
        runTest(dispatcher) {
            val repository = RecordingProjectRepository(result = Result.failure(IllegalStateException("등록 실패")))
            val store = createStore(backgroundScope)
            val viewModel = createReadyViewModel(store = store, projectRepository = repository)

            viewModel.onIntent(QuizCreateIntent.StartCreate)
            runCurrent()

            assertEquals(QuizCreateStage.Ready, viewModel.uiState.value.stage)
            assertFalse(viewModel.uiState.value.isRegistering)
            assertEquals(QuizCreateStatus.Idle, store.state.value.status)
            store.cancel()
        }

    /**
     * 테스트용 프로젝트 식별자와 Store로 ViewModel을 생성한다.
     *
     * @param store 생성 진행 상태를 제공할 테스트 Store
     * @param projectRepository 프로젝트 등록 결과를 제어할 테스트 저장소
     * @return 초기 이해도 선택 단계의 ViewModel
     */
    private fun createViewModel(
        store: QuizCreateStore = createStore(TestScope(dispatcher)),
        projectRepository: ProjectRepository = RecordingProjectRepository(),
    ) = QuizCreateViewModel(
        repositoryUrl = REPOSITORY_URL,
        registerProject = RegisterProjectUseCase(projectRepository),
        createStore = store,
    )

    /**
     * 필수 선택을 완료해 준비 단계까지 이동한 ViewModel을 생성한다.
     *
     * @param store 생성 진행 상태를 제공할 테스트 Store
     * @param projectRepository 프로젝트 등록 결과를 제어할 테스트 저장소
     * @return 생성 시작 인텐트를 받을 수 있는 준비 단계 ViewModel
     */
    private fun createReadyViewModel(
        store: QuizCreateStore,
        projectRepository: ProjectRepository = RecordingProjectRepository(),
    ): QuizCreateViewModel =
        createViewModel(store, projectRepository).apply {
            onIntent(QuizCreateIntent.SelectKnowledge(QuizKnowledgeLevel.Concepts))
            onIntent(QuizCreateIntent.NextClick)
        }

    /**
     * 시간과 코루틴 실행을 제어할 수 있는 테스트 Store를 생성한다.
     *
     * @param scope 진행 상태 갱신 작업을 실행할 테스트 스코프
     * @param nowMillis 테스트에서 사용할 현재 시각 공급자
     * @return 3분으로 고정된 테스트용 생성 Store
     */
    private fun createStore(
        scope: CoroutineScope,
        nowMillis: () -> Long = { 1_000L },
    ): QuizCreateStore =
        QuizCreateStore(
            nowMillis = nowMillis,
            scope = scope,
            durationMillisProvider = { 180_000L },
        )

    private companion object {
        /** 테스트에서 서버 프로젝트로 등록할 GitHub 저장소 URL이다. */
        const val REPOSITORY_URL = "https://github.com/Nexters/Git-It-KMP"
    }
}

/**
 * 프로젝트 등록 요청을 기록하고 준비 상태 프로젝트를 반환한다.
 *
 * @property result 테스트에서 반환할 등록 결과
 */
private class RecordingProjectRepository(
    private val result: Result<ProjectRegistration> =
        Result.success(ProjectRegistration("project-127", ProjectGenerationStatus.Ready)),
) : ProjectRepository {
    /** 마지막으로 전달된 GitHub 저장소 URL이다. */
    var githubRepoUrl: String? = null

    /** 마지막으로 전달된 문제 학습 깊이다. */
    var quizLevel: ProjectQuizLevel? = null

    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> {
        this.githubRepoUrl = githubRepoUrl
        this.quizLevel = quizLevel
        return result
    }

    override suspend fun getProjects(
        page: Int,
        size: Int,
    ): Result<ProjectPage> = error("호출되면 안 됩니다.")

    override suspend fun getProjectDetail(projectId: String): Result<ProjectDetail> = error("호출되면 안 됩니다.")

    override suspend fun deleteProject(projectId: String): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun getLearningSet(
        projectId: String,
        setId: String,
    ): Result<LearningSet> = error("호출되면 안 됩니다.")

    override suspend fun submitChoiceAnswer(
        projectId: String,
        questionId: String,
        selectedIndex: Int,
    ): Result<ChoiceAnswerResult> = error("호출되면 안 됩니다.")

    override suspend fun submitEssayAnswer(
        projectId: String,
        questionId: String,
        text: String,
    ): Result<EssayAnswerResult> = error("호출되면 안 됩니다.")

    override suspend fun bookmarkQuestion(
        projectId: String,
        questionId: String,
        bookmarked: Boolean,
    ): Result<Boolean> = error("호출되면 안 됩니다.")

    override suspend fun getBookmarkedQuestions(projectId: String?): Result<BookmarkedQuestions> = error("호출되면 안 됩니다.")
}
