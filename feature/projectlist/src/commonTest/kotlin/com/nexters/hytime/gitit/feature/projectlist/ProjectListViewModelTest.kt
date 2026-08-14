package com.nexters.hytime.gitit.feature.projectlist

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/** 프로젝트 리스트 ViewModel의 화면 이동 이벤트를 검증한다. */
class ProjectListViewModelTest {
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
