@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.home

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.usecase.GetMemberProfileUseCase
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

/** 홈 화면의 카드 배치와 입력 처리를 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenTest {
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
        runTest(dispatcher) {
            val viewModel = createViewModel()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(HomeIntent.LearningPlayClick("project-1"))

            assertEquals(HomeSideEffect.NavigateToQuiz("project-1"), sideEffect.await())
        }

    /** 홈 카드를 선택하면 프로젝트 상세 이동 이벤트를 발행하는지 검증한다. */
    @Test
    fun learningCardClick_emitsNavigateToProjectDetail() =
        runTest(dispatcher) {
            val viewModel = createViewModel()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onIntent(HomeIntent.LearningCardClick("project-1"))

            assertEquals(HomeSideEffect.NavigateToProjectDetail("project-1"), sideEffect.await())
        }

    /** 프로필 조회가 성공하면 헤더의 이름과 역할을 서버 값으로 채운다. */
    @Test
    fun init_프로필조회에성공하면_헤더이름과역할을채운다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()

            assertEquals("김이박", viewModel.uiState.value.userName)
            assertEquals(CareerLevel.JUNIOR, viewModel.uiState.value.careerLevel)
        }
    }

    private fun createViewModel(): HomeViewModel =
        HomeViewModel(
            getProjects =
                GetProjectsUseCase(
                    FakeHomeProjectRepository(Result.success(ProjectPage(items = emptyList(), hasNext = false))),
                ),
            getMemberProfile =
                GetMemberProfileUseCase(
                    FakeHomeMemberRepository(
                        Result.success(
                            MemberProfile(
                                name = "김이박",
                                email = "gitit@example.com",
                                position = Position.BACKEND,
                                careerLevel = CareerLevel.JUNIOR,
                                thisWeekSolvedCount = 0,
                                thisMonthSolvedCount = 0,
                                streakDays = 0,
                                weeklyChart = emptyList(),
                            ),
                        ),
                    ),
                ),
        )
}
