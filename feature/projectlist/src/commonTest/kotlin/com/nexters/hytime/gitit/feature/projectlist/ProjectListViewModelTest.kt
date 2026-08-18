@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.projectlist

import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectSummary
import com.nexters.hytime.gitit.domain.usecase.DeleteProjectUseCase
import com.nexters.hytime.gitit.domain.usecase.GetProjectsUseCase
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

/** 프로젝트 리스트 ViewModel의 목록 조회와 화면 이동 이벤트를 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class ProjectListViewModelTest {
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

    /** 목록 조회가 성공하면 카드 모델로 변환해 화면 상태를 채운다. */
    @Test
    fun refresh_목록조회에성공하면_카드목록을채운다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()

            val projects = viewModel.uiState.value.projects
            assertEquals(listOf("p1", "p2", "p3"), projects.map(ProjectListItem::id))
            assertEquals("react", projects.first().title)
            assertEquals("https://example.com/a.png", projects.first().thumbnailUrl)
            assertEquals("TypeScript · JavaScript", projects.first().techStack)
            assertEquals("Set 1", projects.first().setLabel)
            assertEquals(40, projects.first().progress)
        }
    }

    /** 목록 조회가 실패하면 빈 목록을 유지한다. */
    @Test
    fun refresh_목록조회에실패하면_빈목록을유지한다() {
        runTest(dispatcher) {
            val repository = FakeProjectListRepository(Result.failure(IllegalStateException("오류")))
            val viewModel =
                ProjectListViewModel(
                    getProjects = GetProjectsUseCase(repository),
                    deleteProject = DeleteProjectUseCase(repository),
                )
            viewModel.refresh()
            runCurrent()

            assertEquals(emptyList(), viewModel.uiState.value.projects)
        }
    }

    /** 화면에 다시 진입하면 서버의 최신 진행률로 목록을 갱신한다. */
    @Test
    fun refresh_다시호출하면_최신진행률로갱신한다() {
        runTest(dispatcher) {
            val repository = FakeProjectListRepository(Result.success(PAGE))
            val viewModel =
                ProjectListViewModel(
                    getProjects = GetProjectsUseCase(repository),
                    deleteProject = DeleteProjectUseCase(repository),
                )
            viewModel.refresh()
            runCurrent()

            repository.pageResult = Result.success(PAGE.copy(items = PAGE.items.map { it.copy(overallProgressPercent = 80) }))
            viewModel.refresh()
            runCurrent()

            assertEquals(
                listOf(80, 80, 80),
                viewModel.uiState.value.projects
                    .map(ProjectListItem::progress),
            )
        }
    }

    /** 삭제 메뉴와 뒤로가기가 백스택 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun deleteModeIntent_clickAndBack_emitsNavigationEvents() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            val navigateToDelete = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(ProjectListIntent.DeleteMenuClick)
            assertEquals(ProjectListSideEffect.NavigateToProjectDelete, navigateToDelete.await())

            val navigateBack = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }
            viewModel.onIntent(ProjectListIntent.DeleteModeBackClick)
            assertEquals(ProjectListSideEffect.NavigateBack, navigateBack.await())
        }
    }

    /** 삭제 확인 모달에서 확인해야 선택한 프로젝트가 목록에서 제거되는지 검증한다. */
    @Test
    fun confirmDeleteClick_projectSelected_removesOnlySelectedProject() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            val deletedProjectId =
                viewModel.uiState.value.projects
                    .first()
                    .id

            viewModel.onIntent(ProjectListIntent.DeleteProjectClick(deletedProjectId))

            assertEquals(deletedProjectId, viewModel.uiState.value.pendingDeleteProjectId)
            assertEquals(3, viewModel.uiState.value.projects.size)

            viewModel.onIntent(ProjectListIntent.ConfirmDeleteClick)
            runCurrent()

            assertFalse(
                viewModel.uiState.value.projects
                    .any { it.id == deletedProjectId },
            )
            assertEquals(2, viewModel.uiState.value.projects.size)
            assertEquals(null, viewModel.uiState.value.pendingDeleteProjectId)
        }
    }

    /** 삭제 확인 모달을 취소하면 프로젝트 목록을 유지하는지 검증한다. */
    @Test
    fun dismissDeleteClick_projectSelected_keepsProjects() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            val deletedProjectId =
                viewModel.uiState.value.projects
                    .first()
                    .id

            viewModel.onIntent(ProjectListIntent.DeleteProjectClick(deletedProjectId))
            viewModel.onIntent(ProjectListIntent.DismissDeleteClick)

            assertEquals(3, viewModel.uiState.value.projects.size)
            assertEquals(null, viewModel.uiState.value.pendingDeleteProjectId)
        }
    }

    /** 프로젝트 카드의 플레이 버튼이 문제 풀이 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun playProjectClick_emitsNavigateToQuiz() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(ProjectListIntent.PlayProjectClick("project-1"))

            assertEquals(ProjectListSideEffect.NavigateToQuiz("project-1"), sideEffect.await())
        }
    }

    /** 프로젝트 카드를 선택하면 프로젝트 상세 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun projectClick_emitsNavigateToProjectDetail() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(ProjectListIntent.ProjectClick("project-1"))

            assertEquals(ProjectListSideEffect.NavigateToProjectDetail("project-1"), sideEffect.await())
        }
    }

    /** 삭제 요청이 실패하면 목록을 유지하고 모달만 닫는다. */
    @Test
    fun confirmDeleteClick_삭제요청이실패하면_목록을유지한다() {
        runTest(dispatcher) {
            val repository = FakeProjectListRepository(Result.success(PAGE), Result.failure(IllegalStateException("오류")))
            val viewModel =
                ProjectListViewModel(
                    getProjects = GetProjectsUseCase(repository),
                    deleteProject = DeleteProjectUseCase(repository),
                )
            viewModel.refresh()
            runCurrent()

            viewModel.onIntent(ProjectListIntent.DeleteProjectClick("p1"))
            viewModel.onIntent(ProjectListIntent.ConfirmDeleteClick)
            runCurrent()

            assertEquals("p1", repository.deletedProjectId)
            assertEquals(3, viewModel.uiState.value.projects.size)
            assertEquals(null, viewModel.uiState.value.pendingDeleteProjectId)
        }
    }

    private fun createViewModel(): ProjectListViewModel {
        val repository = FakeProjectListRepository(Result.success(PAGE))
        return ProjectListViewModel(
            getProjects = GetProjectsUseCase(repository),
            deleteProject = DeleteProjectUseCase(repository),
        ).also(ProjectListViewModel::refresh)
    }

    private companion object {
        private val PAGE =
            ProjectPage(
                items =
                    listOf("p1", "p2", "p3").map { id ->
                        ProjectSummary(
                            projectId = id,
                            repositoryName = "react",
                            repositoryImageUrl = "https://example.com/a.png",
                            techStack = listOf("TypeScript", "JavaScript"),
                            currentSetLabel = "Set 1",
                            currentSetTitle = "라우팅 흐름 따라가기",
                            nextSetId = "s1",
                            nextQuestionId = "q1",
                            overallProgressPercent = 40,
                        )
                    },
                hasNext = false,
            )
    }
}
