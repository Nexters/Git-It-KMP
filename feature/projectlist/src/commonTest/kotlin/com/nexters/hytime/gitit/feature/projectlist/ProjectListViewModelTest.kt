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

    /** 삭제 버튼이 선택한 프로젝트만 목록에서 제거하는지 검증한다. */
    @Test
    fun deleteProjectClick_projectSelected_removesOnlySelectedProject() {
        val viewModel = ProjectListViewModel()
        val deletedProjectId =
            viewModel.uiState.value.projects
                .first()
                .id

        viewModel.onIntent(ProjectListIntent.DeleteProjectClick(deletedProjectId))

        assertFalse(
            viewModel.uiState.value.projects
                .any { it.id == deletedProjectId },
        )
        assertEquals(2, viewModel.uiState.value.projects.size)
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
}
