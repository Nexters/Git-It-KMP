package com.nexters.hytime.gitit.feature.home

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/** 홈 화면의 카드 배치와 입력 처리를 검증한다. */
class HomeScreenTest {
    /** 화면 높이에 따라 카드 크기가 Figma의 최소·최대 범위로 제한된다. */
    @Test
    fun learningCardSize_viewportHeight_returnsMinAndMaxSize() {
        assertEquals(DpSize(154.dp, 192.dp), learningCardSize(700.dp))
        assertEquals(DpSize(209.dp, 260.dp), learningCardSize(874.dp))
        assertEquals(DpSize(209.dp, 260.dp), learningCardSize(1_000.dp))
    }

    /** 현재 카드와 다음 카드가 Figma에 정의된 각도를 사용한다. */
    @Test
    fun learningCardAngle_currentAndAdjacent_returnsFigmaAngles() {
        assertEquals(0f, learningCardAngle(page = 0, pageOffset = 0f))
        assertEquals(16f, learningCardAngle(page = 1, pageOffset = 1f))
        assertEquals(-12f, learningCardAngle(page = 2, pageOffset = 1f))
    }

    /** 스와이프 중인 카드는 페이지 진행도만큼 각도가 연속으로 줄어든다. */
    @Test
    fun learningCardAngle_duringSwipe_interpolatesWithPageOffset() {
        assertEquals(8f, learningCardAngle(page = 1, pageOffset = 0.5f))
    }

    /** 빈 카드가 일반 화면과 폴드 화면의 오른쪽 끝까지 이어지는 개수로 계산된다. */
    @Test
    fun emptyLearningProjectCardCount_viewportWidth_fillsTrailingEdge() {
        assertEquals(3, emptyLearningProjectCardCount(360.dp))
        assertEquals(5, emptyLearningProjectCardCount(700.dp))
    }

    /** 홈 카드의 재생 버튼이 문제 풀이 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun learningPlayClick_emitsNavigateToQuiz() =
        runBlocking {
            val viewModel = HomeViewModel()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(HomeIntent.LearningPlayClick("project-1"))

            assertEquals(HomeSideEffect.NavigateToQuiz("project-1"), sideEffect.await())
        }

    /** 홈 카드를 선택하면 프로젝트 상세 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun learningCardClick_emitsNavigateToProjectDetail() =
        runBlocking {
            val viewModel = HomeViewModel()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(HomeIntent.LearningCardClick("project-1"))

            assertEquals(HomeSideEffect.NavigateToProjectDetail("project-1"), sideEffect.await())
        }
}
