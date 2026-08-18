package com.nexters.hytime.gitit.feature.quiz.create.session

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** 앱 프로세스 범위 문제 생성 세션의 진행 상태 계산을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizCreateStoreTest {
    /** 새 생성 세션은 메모리에서 대상 프로젝트의 진행 상태를 시작한다. */
    @Test
    fun start_newSession_startsProjectProgress() =
        runTest {
            val store =
                QuizCreateStore(
                    nowMillis = { 12_345L },
                    scope = backgroundScope,
                    durationMillisProvider = { 240_000L },
                )

            store.start("project-127")

            assertEquals("project-127", store.state.value.projectId)
            assertEquals(QuizCreateStatus.InProgress, store.state.value.status)
            assertEquals(0, store.state.value.progressPercent)
            store.cancel()
        }

    /** 새 세션으로 교체한 뒤 이전 세션의 예약 갱신이 현재 상태를 덮어쓰지 않는다. */
    @Test
    fun start_replacedSession_ignoresPreviousSessionUpdate() =
        runTest {
            var nowMillis = 1_000L
            val store =
                QuizCreateStore(
                    nowMillis = { nowMillis },
                    scope = backgroundScope,
                    durationMillisProvider = { 180_000L },
                )
            store.start("project-old")
            store.start("project-new")

            nowMillis += 90_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals("project-new", store.state.value.projectId)
            assertEquals(50, store.state.value.progressPercent)
            store.cancel()
        }

    /** 새 Store는 이전 프로세스의 생성 상태를 복원하지 않는다. */
    @Test
    fun initialize_newStore_startsIdle() =
        runTest {
            val previousStore =
                QuizCreateStore(
                    nowMillis = { 1_000L },
                    scope = backgroundScope,
                )
            previousStore.start("project-127")
            val newStore =
                QuizCreateStore(
                    nowMillis = { 101_000L },
                    scope = backgroundScope,
                )

            assertEquals(QuizCreateStatus.Idle, newStore.state.value.status)
            assertEquals(null, newStore.state.value.projectId)
            assertEquals(0, newStore.state.value.progressPercent)
            previousStore.cancel()
            newStore.cancel()
        }

    /** 생성 시작과 완료는 복원용 프로젝트 식별자를 각각 저장하고 제거한다. */
    @Test
    fun persistentProject_startAndComplete_updatesStorage() =
        runTest {
            val storage = InMemoryPendingQuizCreationStorage()
            val store =
                QuizCreateStore(
                    nowMillis = { 1_000L },
                    scope = backgroundScope,
                    storage = storage,
                )

            store.start("project-127")
            assertEquals("project-127", storage.projectId)

            store.updateFromServer("project-127", ProjectGenerationStatus.Completed)
            assertNull(storage.projectId)
            assertEquals(QuizCreateStatus.Completing, store.state.value.status)
        }

    /** 재시작 뒤 진행 상태는 복원하고 재시도 정보가 없는 실패 상태는 로딩만 종료한다. */
    @Test
    fun updateFromServer_restoredSession_resumesAndStopsWithoutRetryModal() =
        runTest {
            val storage = InMemoryPendingQuizCreationStorage().apply { projectId = "project-127" }
            val store =
                QuizCreateStore(
                    nowMillis = { 1_000L },
                    scope = backgroundScope,
                    storage = storage,
                )

            store.updateFromServer("project-127", ProjectGenerationStatus.Started)
            assertEquals(QuizCreateStatus.InProgress, store.state.value.status)

            store.updateFromServer("project-127", ProjectGenerationStatus.Failed)
            assertEquals(QuizCreateStatus.Idle, store.state.value.status)
            assertNull(storage.projectId)
        }

    /** 거절 상태는 복원용 식별자를 제거하고 재시도 모달 없이 terminal 상태로 전환한다. */
    @Test
    fun updateFromServer_rejected_stopsWithoutFailureState() =
        runTest {
            val storage = InMemoryPendingQuizCreationStorage()
            val store =
                QuizCreateStore(
                    nowMillis = { 1_000L },
                    scope = backgroundScope,
                    storage = storage,
                )
            store.start(
                projectId = "project-127",
                request = QuizCreateRequest("https://github.com/Nexters/Git-It-KMP", ProjectQuizLevel.L2),
            )

            store.updateFromServer("project-127", ProjectGenerationStatus.Rejected)

            assertEquals(QuizCreateStatus.Rejected, store.state.value.status)
            assertNull(storage.projectId)
        }

    /** 서버 완료 신호가 없으면 전체 시간이 지나도 98%에서 대기한다. */
    @Test
    fun update_expiredSession_waitsAtNinetyEightPercent() =
        runTest {
            var nowMillis = 1_000L
            val store =
                QuizCreateStore(
                    nowMillis = { nowMillis },
                    scope = backgroundScope,
                    durationMillisProvider = { 180_000L },
                )
            store.start("project-127")
            nowMillis += 180_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals(QuizCreateStatus.InProgress, store.state.value.status)
            assertEquals(98, store.state.value.progressPercent)
            store.cancel()
        }

    /** 5분 이내 완료 신호가 오면 현재 진행률부터 빠르게 100%까지 채운다. */
    @Test
    fun complete_beforeTimelineEnd_animatesFromCurrentProgressToComplete() =
        runTest {
            var nowMillis = 1_000L
            val store =
                QuizCreateStore(
                    nowMillis = { nowMillis },
                    scope = backgroundScope,
                    durationMillisProvider = { 300_000L },
                )
            store.start("project-127")
            nowMillis += 120_000L
            advanceTimeBy(1_000L)
            runCurrent()
            assertEquals(40, store.state.value.progressPercent)

            store.complete("project-127")
            assertEquals(QuizCreateStatus.Completing, store.state.value.status)

            nowMillis += 1_000L
            advanceTimeBy(1_000L)
            runCurrent()
            assertEquals(QuizCreateStatus.Completed, store.state.value.status)
            assertEquals(100, store.state.value.progressPercent)
        }

    /** 5분을 넘긴 뒤 완료 신호가 오면 98% 대기 상태에서 100%로 채운다. */
    @Test
    fun complete_afterTimelineEnd_animatesFromNinetyEightPercentToComplete() =
        runTest {
            var nowMillis = 1_000L
            val store =
                QuizCreateStore(
                    nowMillis = { nowMillis },
                    scope = backgroundScope,
                    durationMillisProvider = { 300_000L },
                )
            store.start("project-127")
            nowMillis += 360_000L
            advanceTimeBy(1_000L)
            runCurrent()
            assertEquals(98, store.state.value.progressPercent)

            store.complete("project-127")
            nowMillis += 1_000L
            advanceTimeBy(1_000L)
            runCurrent()

            assertEquals(QuizCreateStatus.Completed, store.state.value.status)
            assertEquals(100, store.state.value.progressPercent)
        }

    /** 서버 실패 신호는 현재 메모리 세션을 실패 상태로 바꾸고 갱신 작업을 종료한다. */
    @Test
    fun fail_activeSession_updatesFailureState() =
        runTest {
            val store =
                QuizCreateStore(
                    nowMillis = { 1_000L },
                    scope = backgroundScope,
                    durationMillisProvider = { 300_000L },
                )
            store.start("project-127")

            store.fail("project-127")

            assertEquals(QuizCreateStatus.Failed, store.state.value.status)
            assertEquals("project-127", store.state.value.projectId)
            store.cancel()
        }

    /** 실패 세션은 서버 재등록 입력을 한 번만 제공하고 재시도 실패 후 다시 제공한다. */
    @Test
    fun beginRetry_failedSession_controlsDuplicateRetry() =
        runTest {
            val store = QuizCreateStore(nowMillis = { 1_000L }, scope = backgroundScope)
            val request = QuizCreateRequest("https://github.com/Nexters/Git-It-KMP", ProjectQuizLevel.L2)
            store.start("project-127", request)
            store.fail("project-127")

            assertEquals(request, store.beginRetry())
            assertNull(store.beginRetry())
            store.retryFailed()
            assertEquals(request, store.beginRetry())
            store.cancel()
        }
}
