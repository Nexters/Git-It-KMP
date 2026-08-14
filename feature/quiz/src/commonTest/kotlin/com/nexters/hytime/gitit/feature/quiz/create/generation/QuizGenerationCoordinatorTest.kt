package com.nexters.hytime.gitit.feature.quiz.create.generation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** 앱 범위 문제 생성 세션의 저장과 복원 동작을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizGenerationCoordinatorTest {
    /** 새 생성 세션은 시작 시각과 전체 시간을 저장한다. */
    @Test
    fun start_newSession_persistsTimingInformation() =
        runTest {
            val store = RecordingQuizGenerationSessionStore()
            val coordinator =
                QuizGenerationCoordinator(
                    store = store,
                    nowMillis = { 12_345L },
                    scope = this,
                    durationMillisProvider = { 240_000L },
                )

            coordinator.start("project-127")

            assertEquals(QuizGenerationSession("project-127", 12_345L, 240_000L), store.session)
            assertEquals(QuizGenerationStatus.Generating, coordinator.state.value.status)
            coordinator.cancel()
        }

    /** 저장된 세션은 현재 시각을 기준으로 진행률과 홈 모달을 복원한다. */
    @Test
    fun initialize_savedSession_restoresElapsedProgress() =
        runTest {
            val store =
                RecordingQuizGenerationSessionStore(
                    QuizGenerationSession("project-127", startedAtMillis = 1_000L, durationMillis = 200_000L),
                )
            val coordinator =
                QuizGenerationCoordinator(
                    store = store,
                    nowMillis = { 101_000L },
                    scope = this,
                )

            assertEquals(QuizGenerationStatus.Generating, coordinator.state.value.status)
            assertEquals(50, coordinator.state.value.progressPercent)
            assertTrue(coordinator.state.value.isHomeModalVisible)
            coordinator.cancel()
        }

    /** 서버 완료 신호가 없으면 전체 시간이 지나도 98%에서 대기한다. */
    @Test
    fun initialize_expiredSession_waitsAtNinetyEightPercent() =
        runTest {
            val coordinator =
                QuizGenerationCoordinator(
                    store =
                        RecordingQuizGenerationSessionStore(
                            QuizGenerationSession("project-127", startedAtMillis = 1_000L, durationMillis = 180_000L),
                        ),
                    nowMillis = { 181_000L },
                    scope = this,
                )

            assertEquals(QuizGenerationStatus.Generating, coordinator.state.value.status)
            assertEquals(98, coordinator.state.value.progressPercent)
            assertTrue(coordinator.state.value.isHomeModalVisible)
            coordinator.cancel()
        }

    /** 5분 이내 완료 신호가 오면 현재 진행률부터 빠르게 100%까지 채운다. */
    @Test
    fun complete_beforeTimelineEnd_animatesFromCurrentProgressToComplete() =
        runTest {
            var nowMillis = 1_000L
            val coordinator =
                QuizGenerationCoordinator(
                    store = RecordingQuizGenerationSessionStore(),
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
                    store = RecordingQuizGenerationSessionStore(),
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

    /** 서버 실패 신호는 현재 세션을 실패 상태로 저장하고 갱신 작업을 종료한다. */
    @Test
    fun fail_activeSession_persistsFailureState() =
        runTest {
            val store = RecordingQuizGenerationSessionStore()
            val coordinator =
                QuizGenerationCoordinator(
                    store = store,
                    nowMillis = { 1_000L },
                    scope = this,
                    durationMillisProvider = { 300_000L },
                )
            coordinator.start("project-127")

            coordinator.fail("project-127")

            assertEquals(QuizGenerationStatus.Failed, coordinator.state.value.status)
            assertEquals(true, store.session?.failed)
            coordinator.cancel()
        }

    /** 실패한 세션은 앱 재시작 뒤 홈 실패 모달 상태로 복원된다. */
    @Test
    fun initialize_failedSession_restoresFailureModal() =
        runTest {
            val coordinator =
                QuizGenerationCoordinator(
                    store =
                        RecordingQuizGenerationSessionStore(
                            QuizGenerationSession(
                                projectId = "project-127",
                                startedAtMillis = 1_000L,
                                durationMillis = 300_000L,
                                failed = true,
                            ),
                        ),
                    nowMillis = { 2_000L },
                    scope = this,
                )

            assertEquals(QuizGenerationStatus.Failed, coordinator.state.value.status)
            assertTrue(coordinator.state.value.isHomeModalVisible)
            coordinator.cancel()
        }
}

/**
 * 마지막으로 저장한 세션을 테스트에서 확인할 수 있는 저장소다.
 *
 * @property session 현재 저장된 세션
 */
private class RecordingQuizGenerationSessionStore(
    var session: QuizGenerationSession? = null,
) : QuizGenerationSessionStore {
    /** 마지막으로 저장된 생성 세션을 반환한다. */
    override fun load(): QuizGenerationSession? = session

    /** 전달받은 생성 세션을 마지막 세션으로 기록한다. */
    override fun save(session: QuizGenerationSession) {
        this.session = session
    }

    /** 기록된 생성 세션을 제거한다. */
    override fun clear() {
        session = null
    }
}
