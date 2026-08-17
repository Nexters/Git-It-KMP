@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.my

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.usecase.GetMemberProfileUseCase
import com.nexters.hytime.gitit.domain.usecase.UpdatePositionUseCase
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

/** 개발 분야 선택 화면의 선택·저장 흐름을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsPositionViewModelTest {
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

    /** 진입하면 프로필의 현재 개발 분야를 선택 상태로 표시한다. */
    @Test
    fun init_프로필을조회하면_현재분야를선택한다() {
        runTest(dispatcher) {
            val repository = FakeMemberRepository(profileResult = Result.success(PROFILE))
            val viewModel = createViewModel(repository)
            runCurrent()

            assertEquals(Position.BACKEND, viewModel.uiState.value.selected)
        }
    }

    /** 선택을 바꾸고 뒤로가면 서버에 저장한 뒤 이전 화면으로 이동한다. */
    @Test
    fun backClick_선택이바뀌면_저장하고이전화면으로이동한다() {
        runTest(dispatcher) {
            val repository = FakeMemberRepository(profileResult = Result.success(PROFILE))
            val viewModel = createViewModel(repository)
            runCurrent()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onPositionClick(Position.ANDROID)
            viewModel.onBackClick()
            runCurrent()

            assertEquals(Position.ANDROID, repository.updatedPosition)
            assertEquals(SettingsPositionSideEffect.NavigateBack, sideEffect.await())
        }
    }

    /** 선택이 그대로면 저장 요청 없이 이전 화면으로 이동한다. */
    @Test
    fun backClick_선택이그대로면_저장하지않고이동한다() {
        runTest(dispatcher) {
            val repository = FakeMemberRepository(profileResult = Result.success(PROFILE))
            val viewModel = createViewModel(repository)
            runCurrent()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onBackClick()
            runCurrent()

            assertEquals(null, repository.updatedPosition)
            assertEquals(SettingsPositionSideEffect.NavigateBack, sideEffect.await())
        }
    }

    /** 저장이 실패해도 이전 화면으로 이동한다. */
    @Test
    fun backClick_저장이실패해도_이전화면으로이동한다() {
        runTest(dispatcher) {
            val repository =
                FakeMemberRepository(
                    profileResult = Result.success(PROFILE),
                    updateResult = Result.failure(IllegalStateException("네트워크 오류")),
                )
            val viewModel = createViewModel(repository)
            runCurrent()
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onPositionClick(Position.IOS)
            viewModel.onBackClick()
            runCurrent()

            assertEquals(SettingsPositionSideEffect.NavigateBack, sideEffect.await())
        }
    }

    private fun createViewModel(repository: FakeMemberRepository): SettingsPositionViewModel =
        SettingsPositionViewModel(
            getMemberProfile = GetMemberProfileUseCase(repository),
            updatePosition = UpdatePositionUseCase(repository),
        )

    private companion object {
        private val PROFILE =
            MemberProfile(
                name = "김이박",
                email = "gitit@example.com",
                position = Position.BACKEND,
                careerLevel = CareerLevel.JUNIOR,
                thisWeekSolvedCount = 0,
                thisMonthSolvedCount = 0,
                streakDays = 0,
                weeklyChart = emptyList(),
            )
    }
}
