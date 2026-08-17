@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.my

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DaySolvedCount
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.repository.MemberRepository
import com.nexters.hytime.gitit.domain.usecase.GetMemberProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** 마이 화면 상태가 회원 프로필 조회 결과로 채워지는지 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
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

    /** 프로필 조회가 성공하면 라벨 변환을 거쳐 화면 상태를 채운다. */
    @Test
    fun init_프로필조회에성공하면_화면상태를채운다() {
        runTest(dispatcher) {
            val viewModel = MyViewModel(GetMemberProfileUseCase(FakeMemberRepository(Result.success(PROFILE))))
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals("김이박", state.profile.name)
            assertEquals("gitit@example.com", state.profile.email)
            assertEquals("Back-end", state.profile.developmentField)
            assertEquals("주니어", state.profile.learningLevel)
            assertEquals(listOf("13문제", "47문제", "7일"), state.stats.map(MyStudyStat::value))
            assertEquals(listOf("월", "화"), state.weeklyStudy.map(MyWeeklyStudy::day))
            assertEquals(listOf(3, 0), state.weeklyStudy.map(MyWeeklyStudy::solvedCount))
        }
    }

    /** 큐레이션 전이라 프로필 값이 비어 있으면 빈 라벨로 표시한다. */
    @Test
    fun init_큐레이션전프로필이면_빈라벨로표시한다() {
        val emptyProfile =
            PROFILE.copy(name = null, email = null, position = null, careerLevel = null)

        runTest(dispatcher) {
            val viewModel = MyViewModel(GetMemberProfileUseCase(FakeMemberRepository(Result.success(emptyProfile))))
            runCurrent()

            val profile = viewModel.uiState.value.profile
            assertEquals("", profile.name)
            assertEquals("", profile.developmentField)
            assertEquals("", profile.learningLevel)
        }
    }

    /** 프로필 조회가 실패하면 초기 빈 상태를 유지한다. */
    @Test
    fun init_프로필조회에실패하면_빈상태를유지한다() {
        runTest(dispatcher) {
            val viewModel =
                MyViewModel(GetMemberProfileUseCase(FakeMemberRepository(Result.failure(IllegalStateException("네트워크 오류")))))
            runCurrent()

            assertEquals(MyUiState(), viewModel.uiState.value)
        }
    }

    private companion object {
        private val PROFILE =
            MemberProfile(
                name = "김이박",
                email = "gitit@example.com",
                position = Position.BACKEND,
                careerLevel = CareerLevel.JUNIOR,
                thisWeekSolvedCount = 13,
                thisMonthSolvedCount = 47,
                streakDays = 7,
                weeklyChart =
                    listOf(
                        DaySolvedCount(dayLabel = "월", count = 3),
                        DaySolvedCount(dayLabel = "화", count = 0),
                    ),
            )
    }
}
