@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.projectdetail

import com.nexters.hytime.gitit.domain.model.LearningSetSummary
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.usecase.DeleteProjectUseCase
import com.nexters.hytime.gitit.domain.usecase.GetProjectDetailUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 프로젝트 상세 ViewModel의 상세 조회와 상태 변경을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class ProjectDetailViewModelTest {
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

    /** 상세 조회가 성공하면 프로젝트 정보와 세트 목록을 화면 상태로 변환한다. */
    @Test
    fun refresh_상세조회에성공하면_화면상태를채운다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals("react", state.project?.name)
            assertEquals("3.6k", state.project?.starCount)
            assertEquals("TypeScript · JavaScript", state.project?.techStack)
            assertEquals(60, state.totalProgress)
            assertEquals(listOf("s1"), state.learningSets.map(LearningSetItem::id))
            assertEquals("Set 1", state.learningSets.first().title)
            assertEquals("라우팅 흐름 따라가기", state.learningSets.first().description)
            assertEquals(60, state.learningSets.first().progress)
            assertEquals(5, state.learningSets.first().totalSteps)
        }
    }

    /** 상세 조회가 실패하면 로딩 상태를 유지한다. */
    @Test
    fun refresh_상세조회에실패하면_로딩상태를유지한다() {
        runTest(dispatcher) {
            val repository = FakeProjectDetailRepository(Result.failure(IllegalStateException("오류")))
            val viewModel =
                ProjectDetailViewModel(
                    projectId = "project-1",
                    getProjectDetail = GetProjectDetailUseCase(repository),
                    deleteProject = DeleteProjectUseCase(repository),
                )
            viewModel.refresh()
            runCurrent()

            assertNull(viewModel.uiState.value.project)
        }
    }

    /** 화면에 다시 진입하면 서버의 최신 진행률로 상세 정보를 갱신한다. */
    @Test
    fun refresh_다시호출하면_최신진행률로갱신한다() {
        runTest(dispatcher) {
            val repository = FakeProjectDetailRepository(Result.success(DETAIL))
            val viewModel =
                ProjectDetailViewModel(
                    projectId = "project-1",
                    getProjectDetail = GetProjectDetailUseCase(repository),
                    deleteProject = DeleteProjectUseCase(repository),
                )
            viewModel.refresh()
            runCurrent()

            repository.detailResult =
                Result.success(
                    DETAIL.copy(
                        overallProgressPercent = 80,
                        sets = DETAIL.sets.map { it.copy(completedCount = 4) },
                    ),
                )
            viewModel.refresh()
            runCurrent()

            assertEquals(80, viewModel.uiState.value.totalProgress)
            assertEquals(
                80,
                viewModel.uiState.value.learningSets
                    .single()
                    .progress,
            )
        }
    }

    /** 더보기 메뉴 인텐트가 노출 상태를 켰다가 다시 끄는지 검증한다. */
    @Test
    fun onMoreMenuClick_togglesMenuVisibility() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()

            viewModel.onMoreMenuClick()
            assertTrue(viewModel.uiState.value.showMoreMenu)

            viewModel.onMoreMenuClick()
            assertFalse(viewModel.uiState.value.showMoreMenu)
        }
    }

    /** 더보기 메뉴 닫기 인텐트가 메뉴 노출 상태를 false로 바꾸는지 검증한다. */
    @Test
    fun onDismissMoreMenu_hidesMenu() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()

            viewModel.onMoreMenuClick()
            viewModel.onDismissMoreMenu()

            assertFalse(viewModel.uiState.value.showMoreMenu)
        }
    }

    /** 서버 삭제가 성공해야 홈 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun onDeleteProjectClick_삭제에성공하면_홈이동이벤트를발행한다() {
        runTest(dispatcher) {
            val repository = FakeProjectDetailRepository(Result.success(DETAIL))
            val viewModel =
                ProjectDetailViewModel(
                    projectId = "project-1",
                    getProjectDetail = GetProjectDetailUseCase(repository),
                    deleteProject = DeleteProjectUseCase(repository),
                )
            runCurrent()
            val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

            viewModel.onDeleteProjectClick()
            runCurrent()

            assertEquals("project-1", repository.deletedProjectId)
            assertEquals(ProjectDetailEvent.NavigateToHome, event.await())
        }
    }

    /** 프로젝트 상세의 플레이 버튼이 문제 풀이 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun onQuestionSolvingClick_emitsNavigateToQuiz() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

            viewModel.onQuestionSolvingClick()

            assertEquals(ProjectDetailEvent.NavigateToQuiz("project-1"), event.await())
        }
    }

    /** 스타 수 축약 표기의 경계 값을 검증한다. */
    @Test
    fun formatStarCount_경계값을_k표기로변환한다() {
        assertEquals("999", formatStarCount(999))
        assertEquals("1k", formatStarCount(1_000))
        assertEquals("3.6k", formatStarCount(3_649))
        assertEquals("12.3k", formatStarCount(12_345))
    }

    private fun createViewModel(): ProjectDetailViewModel {
        val repository = FakeProjectDetailRepository(Result.success(DETAIL))
        return ProjectDetailViewModel(
            projectId = "project-1",
            getProjectDetail = GetProjectDetailUseCase(repository),
            deleteProject = DeleteProjectUseCase(repository),
        ).also(ProjectDetailViewModel::refresh)
    }

    private companion object {
        private val DETAIL =
            ProjectDetail(
                projectId = "project-1",
                repositoryUrl = "https://github.com/facebook/react",
                repositoryName = "react",
                repositoryImageUrl = "https://example.com/a.png",
                starCount = 3_649,
                techStack = listOf("TypeScript", "JavaScript"),
                overallProgressPercent = 60,
                nextProblemId = "q1",
                sets =
                    listOf(
                        LearningSetSummary(
                            setId = "s1",
                            label = "Set 1",
                            title = "라우팅 흐름 따라가기",
                            problemCount = 5,
                            completedCount = 3,
                        ),
                    ),
            )
    }
}
