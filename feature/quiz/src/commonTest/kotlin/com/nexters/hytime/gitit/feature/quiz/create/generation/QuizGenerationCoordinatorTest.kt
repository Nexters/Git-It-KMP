package com.nexters.hytime.gitit.feature.quiz.create.generation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** 앱 프로세스 범위 문제 생성 세션의 진행 상태 계산을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizGenerationCoordinatorTest {
    /** 새 생성 세션은 메모리에서 대상 프로젝트의 진행 상태를 시작한다. */
    @Test
    fun start_newSession_startsProjectProgress() =
        runTest {
            val coordinator =
                QuizGenerationCoordinator(
                    nowMillis = { 12_345L },
                    scope = this,
                    durationMillisProvider = { 240_000L },
                )

            coordinator.start("project-127")

            assertEquals("project-127", coordinator.state.value.projectId)
            assertEquals(QuizGenerationStatus.Generating, coordinator.state.value.status)
            assertEquals(0, coordinator.state.value.progressPercent)
            coordinator.cancel()
        }

    /** 새 코디네이터는 이전 프로세스의 생성 상태를 복원하지 않는다. */
    @Test
    fun initialize_newCoordinator_startsIdle() =
        runTest {
            val previousCoordinator =
                QuizGenerationCoordinator(
                    nowMillis = { 1_000L },
                    scope = this,
                )
            previousCoordinator.start("project-127")
            val newCoordinator =
                QuizGenerationCoordinator(
                    nowMillis = { 101_000L },
                    scope = this,
                )

            assertEquals(QuizGenerationStatus.Idle, newCoordinator.state.value.status)
            assertEquals(null, newCoordinator.state.value.projectId)
            assertEquals(0, newCoordinator.state.value.progressPercent)
            previousCoordinator.cancel()
            newCoordinator.cancel()
        }

    /** 서버 완료 신호가 없으면 전체 시간이 지나도 98%에서 대기한다. */
    @Test
    fun update_expiredSession_waitsAtNinetyEightPercent() =
        runTest {
            var nowMillis = 1_000L
            val coordinator =
                QuizGenerationCoordinator(
                    nowMillis = { nowMillis },
                    scope = this,
                    durationMillisProvider = { 180_000L },
                )
            coordinator.start("project-127")
            nowMillis += 180_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals(QuizGenerationStatus.Generating, coordinator.state.value.status)
            assertEquals(98, coordinator.state.value.progressPercent)
            coordinator.cancel()
        }

    /** 5분 이내 완료 신호가 오면 현재 진행률부터 빠르게 100%까지 채운다. */
    @Test
    fun complete_beforeTimelineEnd_animatesFromCurrentProgressToComplete() =
        runTest {
            var nowMillis = 1_000L
            val coordinator =
                QuizGenerationCoordinator(
                    nowMillis = { nowMillis },
                    scope = this,
                    durationMillisProvider = { 300_000L },
                )
            coordinator.start("project-127")
            nowMillis += 120_000L
            advanceTimeBy(1_000L)
            runCurrent()
            assertEquals(40, coordinator.state.value.progressPercent)

            coordinator.complete("project-127")
            assertEquals(QuizGenerationStatus.Completing, coordinator.state.value.status)

            nowMillis += 1_000L
            advanceTimeBy(1_000L)
            runCurrent()
            assertEquals(QuizGenerationStatus.Completed, coordinator.state.value.status)
            assertEquals(100, coordinator.state.value.progressPercent)
        }

    /** 5분을 넘긴 뒤 완료 신호가 오면 98% 대기 상태에서 100%로 채운다. */
    @Test
    fun complete_afterTimelineEnd_animatesFromNinetyEightPercentToComplete() =
        runTest {
            var nowMillis = 1_000L
            val coordinator =
                QuizGenerationCoordinator(
                    nowMillis = { nowMillis },
                    scope = this,
                    durationMillisProvider = { 300_000L },
                )
            coordinator.start("project-127")
            nowMillis += 360_000L
            advanceTimeBy(1_000L)
            runCurrent()
            assertEquals(98, coordinator.state.value.progressPercent)

            coordinator.complete("project-127")
            nowMillis += 1_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals(QuizGenerationStatus.Completed, coordinator.state.value.status)
            assertEquals(100, coordinator.state.value.progressPercent)
        }

    /** 서버 실패 신호는 현재 메모리 세션을 실패 상태로 바꾸고 갱신 작업을 종료한다. */
    @Test
    fun fail_activeSession_updatesFailureState() =
        runTest {
            val coordinator =
                QuizGenerationCoordinator(
                    nowMillis = { 1_000L },
                    scope = this,
                    durationMillisProvider = { 300_000L },
                )
            coordinator.start("project-127")

            coordinator.fail("project-127")

            assertEquals(QuizGenerationStatus.Failed, coordinator.state.value.status)
            assertEquals("project-127", coordinator.state.value.projectId)
            coordinator.cancel()
        }
}
