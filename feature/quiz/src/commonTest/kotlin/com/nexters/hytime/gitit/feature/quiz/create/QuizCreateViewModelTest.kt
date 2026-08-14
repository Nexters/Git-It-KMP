package com.nexters.hytime.gitit.feature.quiz.create

import com.nexters.hytime.gitit.feature.quiz.create.generation.QuizGenerationCoordinator
import com.nexters.hytime.gitit.feature.quiz.create.generation.QuizGenerationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
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

    /** 이해도와 주제를 선택해야 준비 단계까지 이동한다. */
    @Test
    fun onIntent_requiredSelections_movesToReadyStage() {
        val viewModel = createViewModel()

        viewModel.onIntent(QuizCreateIntent.NextClick)
        assertEquals(QuizCreateStage.Knowledge, viewModel.uiState.value.stage)

        viewModel.onIntent(QuizCreateIntent.SelectKnowledge(QuizKnowledgeLevel.SomeCode))
        viewModel.onIntent(QuizCreateIntent.NextClick)
        viewModel.onIntent(QuizCreateIntent.ToggleTopic(QuizCreateTopic.FeatureFlow))
        viewModel.onIntent(QuizCreateIntent.NextClick)

        assertEquals(QuizCreateStage.Ready, viewModel.uiState.value.stage)
        assertTrue(QuizCreateTopic.FeatureFlow in viewModel.uiState.value.topics)
    }

    /** 같은 문제 주제를 다시 누르면 선택에서 제거한다. */
    @Test
    fun onIntent_sameTopicTwice_removesSelection() {
        val viewModel = createViewModel()

        viewModel.onIntent(QuizCreateIntent.ToggleTopic(QuizCreateTopic.CodeIntent))
        viewModel.onIntent(QuizCreateIntent.ToggleTopic(QuizCreateTopic.CodeIntent))

        assertFalse(QuizCreateTopic.CodeIntent in viewModel.uiState.value.topics)
    }

    /** 생성 진행률은 유효 범위로 보정된다. */
    @Test
    fun onIntent_generationProgress_clampsAndCompletes() =
        runTest(dispatcher) {
            val coordinator = createCoordinator(this)
            val viewModel = createReadyViewModel(coordinator = coordinator)

            viewModel.onIntent(QuizCreateIntent.StartGeneration)
            viewModel.onIntent(
                QuizCreateIntent.GenerationProgressChanged(
                    step = QuizGenerationStep.Questions,
                    progressPercent = 130,
                ),
            )

            assertEquals(QuizCreateStage.Generating, viewModel.uiState.value.stage)
            assertEquals(QuizGenerationStep.Questions, viewModel.uiState.value.generationStep)
            assertEquals(100, viewModel.uiState.value.progressPercent)

            assertEquals(100, viewModel.uiState.value.progressPercent)
            coordinator.cancel()
        }

    /** 생성 실패 신호는 앱 범위 코디네이터의 실패 상태로 전달된다. */
    @Test
    fun onIntent_generationFailure_updatesCoordinatorFailure() =
        runTest(dispatcher) {
            val coordinator = createCoordinator(this)
            val viewModel = createReadyViewModel(coordinator = coordinator)
            viewModel.onIntent(QuizCreateIntent.StartGeneration)
            viewModel.onIntent(QuizCreateIntent.GenerationFailed)
            runCurrent()

            assertEquals(QuizGenerationStatus.Failed, coordinator.state.value.status)
            assertEquals(QuizCreateStage.Generating, viewModel.uiState.value.stage)
            coordinator.cancel()
        }

    /** FCM 완료 신호로 세션이 끝나면 홈 모달을 열고 홈 이동 이벤트를 전달한다. */
    @Test
    fun startGeneration_sessionCompletes_movesToCompletedStage() =
        runTest(dispatcher) {
            var nowMillis = 1_000L
            val coordinator = createCoordinator(scope = this, nowMillis = { nowMillis })
            val viewModel = createReadyViewModel(coordinator)
            val event = backgroundScope.async { viewModel.events.first() }
            runCurrent()

            viewModel.onIntent(QuizCreateIntent.StartGeneration)
            runCurrent()
            coordinator.complete("project-127")
            nowMillis += 1_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals(QuizGenerationStatus.Completed, coordinator.state.value.status)
            assertTrue(coordinator.state.value.isHomeModalVisible)
            assertEquals(100, viewModel.uiState.value.progressPercent)
            assertEquals(QuizCreateEvent.NavigateHome, event.await())
            coordinator.cancel()
        }

    /** 실패 상태를 전달하면 홈 실패 모달과 홈 이동 이벤트로 연결된다. */
    @Test
    fun onIntent_generationFailure_opensHomeFailureModal() =
        runTest(dispatcher) {
            val coordinator = createCoordinator(this)
            val viewModel = createReadyViewModel(coordinator)
            val event = backgroundScope.async { viewModel.events.first() }
            runCurrent()

            viewModel.onIntent(QuizCreateIntent.StartGeneration)
            viewModel.onIntent(QuizCreateIntent.GenerationFailed)
            runCurrent()

            assertEquals(QuizGenerationStatus.Failed, coordinator.state.value.status)
            assertTrue(coordinator.state.value.isHomeModalVisible)
            assertEquals(QuizCreateEvent.NavigateHome, event.await())
            coordinator.cancel()
        }

    /**
     * 테스트용 프로젝트 식별자와 코디네이터로 ViewModel을 생성한다.
     *
     * @param coordinator 생성 진행 상태를 제공할 테스트 코디네이터
     * @return 초기 이해도 선택 단계의 ViewModel
     */
    private fun createViewModel(coordinator: QuizGenerationCoordinator = createCoordinator(TestScope(dispatcher))) =
        QuizCreateViewModel(projectId = "project-127", coordinator = coordinator)

    /**
     * 필수 선택을 완료해 준비 단계까지 이동한 ViewModel을 생성한다.
     *
     * @param coordinator 생성 진행 상태를 제공할 테스트 코디네이터
     * @return 생성 시작 인텐트를 받을 수 있는 준비 단계 ViewModel
     */
    private fun createReadyViewModel(coordinator: QuizGenerationCoordinator): QuizCreateViewModel =
        createViewModel(coordinator).apply {
            onIntent(QuizCreateIntent.SelectKnowledge(QuizKnowledgeLevel.Concepts))
            onIntent(QuizCreateIntent.NextClick)
            onIntent(QuizCreateIntent.ToggleTopic(QuizCreateTopic.ProjectStructure))
            onIntent(QuizCreateIntent.NextClick)
        }

    /**
     * 시간과 코루틴 실행을 제어할 수 있는 테스트 코디네이터를 생성한다.
     *
     * @param scope 진행 상태 갱신 작업을 실행할 테스트 스코프
     * @param nowMillis 테스트에서 사용할 현재 시각 공급자
     * @return 3분으로 고정된 테스트용 생성 코디네이터
     */
    private fun createCoordinator(
        scope: TestScope,
        nowMillis: () -> Long = { 1_000L },
    ): QuizGenerationCoordinator =
        QuizGenerationCoordinator(
            nowMillis = nowMillis,
            scope = scope,
            durationMillisProvider = { 180_000L },
        )
}
