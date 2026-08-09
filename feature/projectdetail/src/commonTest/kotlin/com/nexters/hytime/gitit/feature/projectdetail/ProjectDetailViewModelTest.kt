package com.nexters.hytime.gitit.feature.projectdetail

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 프로젝트 상세 ViewModel의 상태 변경을 검증한다. */
class ProjectDetailViewModelTest {
    /** 더보기 메뉴 인텐트가 노출 상태를 켰다가 다시 끄는지 검증한다. */
    @Test
    fun onMoreMenuClick_togglesMenuVisibility() {
        val viewModel = ProjectDetailViewModel(projectId = "project-1")

        viewModel.onMoreMenuClick()
        assertTrue(viewModel.uiState.value.showMoreMenu)

        viewModel.onMoreMenuClick()
        assertFalse(viewModel.uiState.value.showMoreMenu)
    }

    /** 더보기 메뉴 닫기 인텐트가 메뉴 노출 상태를 false로 바꾸는지 검증한다. */
    @Test
    fun onDismissMoreMenu_hidesMenu() {
        val viewModel = ProjectDetailViewModel(projectId = "project-1")

        viewModel.onMoreMenuClick()
        viewModel.onDismissMoreMenu()

        assertFalse(viewModel.uiState.value.showMoreMenu)
    }
}
