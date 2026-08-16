package com.nexters.hytime.gitit.feature.projectlist

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/** 프로젝트 리스트 ViewModel의 화면 이동 이벤트를 검증한다. */
class ProjectListViewModelTest {
    /** 삭제 메뉴와 뒤로가기가 백스택 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun deleteModeIntent_clickAndBack_emitsNavigationEvents() =
        runBlocking {
            val viewModel = ProjectListViewModel()
            val navigateToDelete = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(ProjectListIntent.DeleteMenuClick)
            assertEquals(ProjectListSideEffect.NavigateToProjectDelete, navigateToDelete.await())

            val navigateBack = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }
            viewModel.onIntent(ProjectListIntent.DeleteModeBackClick)
            assertEquals(ProjectListSideEffect.NavigateBack, navigateBack.await())
        }

    /** 삭제 확인 모달에서 확인해야 선택한 프로젝트가 목록에서 제거되는지 검증한다. */
    @Test
    fun confirmDeleteClick_projectSelected_removesOnlySelectedProject() {
        val viewModel = ProjectListViewModel()
        val deletedProjectId =
            viewModel.uiState.value.projects
                .first()
                .id

        viewModel.onIntent(ProjectListIntent.DeleteProjectClick(deletedProjectId))

        assertEquals(deletedProjectId, viewModel.uiState.value.pendingDeleteProjectId)
        assertEquals(3, viewModel.uiState.value.projects.size)

        viewModel.onIntent(ProjectListIntent.ConfirmDeleteClick)

        assertFalse(
            viewModel.uiState.value.projects
                .any { it.id == deletedProjectId },
        )
        assertEquals(2, viewModel.uiState.value.projects.size)
        assertEquals(null, viewModel.uiState.value.pendingDeleteProjectId)
    }

    /** 삭제 확인 모달을 취소하면 프로젝트 목록을 유지하는지 검증한다. */
    @Test
    fun dismissDeleteClick_projectSelected_keepsProjects() {
        val viewModel = ProjectListViewModel()
        val deletedProjectId =
            viewModel.uiState.value.projects
                .first()
                .id

        viewModel.onIntent(ProjectListIntent.DeleteProjectClick(deletedProjectId))
        viewModel.onIntent(ProjectListIntent.DismissDeleteClick)

        assertEquals(3, viewModel.uiState.value.projects.size)
        assertEquals(null, viewModel.uiState.value.pendingDeleteProjectId)
    }

    /** 프로젝트 카드의 플레이 버튼이 문제 풀이 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun playProjectClick_emitsNavigateToQuiz() =
        runBlocking {
            val viewModel = ProjectListViewModel()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(ProjectListIntent.PlayProjectClick("project-1"))

            assertEquals(ProjectListSideEffect.NavigateToQuiz("project-1"), sideEffect.await())
        }

    /** 프로젝트 카드를 선택하면 프로젝트 상세 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun projectClick_emitsNavigateToProjectDetail() =
        runBlocking {
            val viewModel = ProjectListViewModel()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(ProjectListIntent.ProjectClick("project-1"))

            assertEquals(ProjectListSideEffect.NavigateToProjectDetail("project-1"), sideEffect.await())
        }
}
