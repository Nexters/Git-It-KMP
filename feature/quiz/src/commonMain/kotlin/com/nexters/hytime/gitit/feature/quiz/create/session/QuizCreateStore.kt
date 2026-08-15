package com.nexters.hytime.gitit.feature.quiz.create.session

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateStep
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * 현재 프로세스에서 문제 생성 진행률을 계산하기 위한 실행 세션이다.
 *
 * @property projectId 생성 대상 프로젝트 식별자
 * @property startedAtMillis 생성 시작 시각의 Unix epoch millisecond
 * @property durationMillis 0%에서 100%까지 도달할 전체 시간
 * @property request 실패 시 서버 등록을 다시 요청하는 데 필요한 입력값
 * @property completionReceivedAtMillis 서버 완료 신호를 받은 시각. 아직 받지 않았다면 null
 * @property completionStartProgress 완료 애니메이션을 시작한 진행률. 아직 시작하지 않았다면 null
 * @property failed 서버 실패 신호를 받아 생성이 중단되었는지 여부
 */
private data class QuizCreateSession(
    val projectId: String,
    val startedAtMillis: Long,
    val durationMillis: Long,
    val request: QuizCreateRequest? = null,
    val completionReceivedAtMillis: Long? = null,
    val completionStartProgress: Int? = null,
    val failed: Boolean = false,
)

/**
 * 서버 프로젝트 등록을 다시 요청할 수 있도록 메모리에 유지하는 입력값이다.
 *
 * @property repositoryUrl 프로젝트로 등록할 GitHub 저장소 URL
 * @property quizLevel 사용자가 선택한 문제 학습 깊이
 */
data class QuizCreateRequest(
    val repositoryUrl: String,
    val quizLevel: ProjectQuizLevel,
)

/** 문제 생성 세션의 현재 실행 상태다. */
enum class QuizCreateStatus {
    /** 진행 중인 생성 세션이 없다. */
    Idle,

    /** 표시용 생성 타임라인이 진행 중이다. */
    InProgress,

    /** 서버 완료 신호를 받아 진행률을 빠르게 100%로 채우고 있다. */
    Completing,

    /** 표시용 생성 타임라인이 완료되었다. */
    Completed,

    /** 서버가 생성 실패를 전달해 사용자 선택을 기다리고 있다. */
    Failed,
}

/**
 * 현재 앱 프로세스에서 공유할 문제 생성 진행 상태다.
 *
 * @property projectId 생성 대상 프로젝트 식별자
 * @property status 생성 세션 상태
 * @property step 현재 화면에 표시할 생성 단계
 * @property progressPercent 0..100 범위의 전체 진행률
 */
data class QuizCreateProgressState(
    val projectId: String? = null,
    val status: QuizCreateStatus = QuizCreateStatus.Idle,
    val step: QuizCreateStep = QuizCreateStep.ProjectInfo,
    val progressPercent: Int = 0,
)

/**
 * 문제 생성 세션과 표시용 진행률을 앱 프로세스 메모리에서 관리한다.
 *
 * 모든 변경은 단일 mutation 채널에서 순서대로 실행되어 FCM, 화면, 타이머가 동시에 상태를 변경해도
 * 별도의 잠금 없이 일관된 결과를 유지한다.
 *
 * @param nowMillis 현재 Unix epoch millisecond를 반환하는 함수
 * @param scope 화면 생명주기와 독립적으로 상태 변경과 진행률 갱신을 실행할 앱 범위 코루틴 스코프
 * @param durationMillisProvider 새 생성 세션의 전체 시간을 결정하는 함수
 */
class QuizCreateStore(
    private val nowMillis: () -> Long = ::currentEpochMillis,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val durationMillisProvider: () -> Long = QuizCreateTimeline::randomDurationMillis,
) {
    /** 외부 상태 구독자에게 전달할 현재 생성 상태다. */
    private val mutableState = MutableStateFlow(QuizCreateProgressState())

    /** 홈과 문제 생성 화면이 함께 구독할 현재 생성 상태다. */
    val state: StateFlow<QuizCreateProgressState> = mutableState.asStateFlow()

    /** 여러 실행 컨텍스트에서 전달된 상태 변경을 하나씩 처리하는 채널이다. */
    private val mutations = Channel<() -> Unit>(capacity = Channel.UNLIMITED)

    /** 현재 세션의 표시용 진행률을 주기적으로 갱신하는 작업이다. */
    private var updateJob: Job? = null

    /** 현재 프로세스에서 실행 중인 생성 세션이다. */
    private var session: QuizCreateSession? = null

    /** 동일 실패 세션의 프로젝트 재등록 요청이 진행 중인지 여부다. */
    private var retryInProgress: Boolean = false

    init {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            for (mutation in mutations) mutation()
        }
    }

    /**
     * 기존 세션을 대체하고 새 문제 생성 타임라인을 시작한다.
     *
     * @param projectId 생성 대상 프로젝트 식별자
     * @param request 실패 시 프로젝트 등록 API를 다시 호출하는 데 사용할 입력값
     */
    suspend fun start(
        projectId: String,
        request: QuizCreateRequest? = null,
    ) = mutate {
        startSession(projectId, request)
    }

    /**
     * 프로젝트 등록 응답으로 표시용 생성 타임라인을 시작한다.
     *
     * @param registration 서버가 발급한 프로젝트 ID와 등록 시점 생성 상태
     * @param request 실패 시 동일 조건으로 프로젝트를 다시 등록하기 위한 입력값
     */
    suspend fun start(
        registration: ProjectRegistration,
        request: QuizCreateRequest,
    ) = mutate {
        startSession(registration.projectId, request)
        when (registration.status) {
            ProjectGenerationStatus.Ready,
            ProjectGenerationStatus.Anchored,
            -> Unit
            ProjectGenerationStatus.Failed -> failSession(registration.projectId)
            ProjectGenerationStatus.Completed -> completeSession(registration.projectId)
        }
    }

    /**
     * 실패 상태에서 중복 요청을 막고 서버 재등록에 사용할 입력값을 반환한다.
     *
     * @return 재시도를 시작했다면 메모리에 보관된 요청, 재시도할 수 없으면 null
     */
    suspend fun beginRetry(): QuizCreateRequest? =
        mutate {
            if (mutableState.value.status != QuizCreateStatus.Failed || retryInProgress) return@mutate null
            val request = session?.request ?: return@mutate null
            retryInProgress = true
            request
        }

    /** 서버 프로젝트 재등록이 실패했을 때 같은 세션을 다시 요청할 수 있게 한다. */
    suspend fun retryFailed() =
        mutate {
            if (mutableState.value.status == QuizCreateStatus.Failed) retryInProgress = false
        }

    /**
     * FCM 데이터로 전달된 문제 생성 완료 신호를 현재 세션에 반영한다.
     *
     * @param projectId 완료된 프로젝트 식별자
     */
    suspend fun complete(projectId: String) =
        mutate {
            completeSession(projectId)
        }

    /**
     * 서버가 전달한 문제 생성 실패 신호를 현재 세션에 반영한다.
     *
     * @param projectId 생성에 실패한 프로젝트 식별자
     */
    suspend fun fail(projectId: String) =
        mutate {
            failSession(projectId)
        }

    /** 현재 프로세스에서 실행 중인 생성 세션을 제거한다. */
    suspend fun cancel() =
        mutate {
            updateJob?.cancel()
            updateJob = null
            session = null
            retryInProgress = false
            mutableState.value = QuizCreateProgressState()
        }

    /**
     * 상태 변경을 Store의 단일 mutation 루프에 전달하고 처리 결과를 기다린다.
     *
     * @param mutation Store 내부 상태에 적용할 변경
     * @return mutation이 반환한 결과
     */
    private suspend fun <T> mutate(mutation: () -> T): T {
        val result = CompletableDeferred<Result<T>>()
        mutations.send {
            result.complete(runCatching(mutation))
        }
        return result.await().getOrThrow()
    }

    /**
     * 새 실행 세션을 만들고 진행률 갱신을 시작한다.
     *
     * @param projectId 생성 대상 프로젝트 식별자
     * @param request 실패 시 동일 조건으로 다시 등록할 입력값
     */
    private fun startSession(
        projectId: String,
        request: QuizCreateRequest?,
    ) {
        val newSession =
            QuizCreateSession(
                projectId = projectId,
                startedAtMillis = nowMillis(),
                durationMillis = durationMillisProvider(),
                request = request,
            )
        session = newSession
        retryInProgress = false
        runSession(newSession)
    }

    /**
     * 서버 완료 신호를 현재 세션에 반영한다.
     *
     * @param projectId 완료된 프로젝트 식별자
     */
    private fun completeSession(projectId: String) {
        val currentSession = session ?: return
        if (currentSession.projectId != projectId || currentSession.completionReceivedAtMillis != null || currentSession.failed) return

        val completedSession =
            currentSession.copy(
                completionReceivedAtMillis = nowMillis(),
                completionStartProgress = mutableState.value.progressPercent.coerceIn(0, WAITING_PROGRESS_PERCENT),
            )
        session = completedSession
        runSession(completedSession)
    }

    /**
     * 서버 실패 신호를 현재 세션에 반영한다.
     *
     * @param projectId 생성에 실패한 프로젝트 식별자
     */
    private fun failSession(projectId: String) {
        val currentSession = session ?: return
        if (currentSession.projectId != projectId || currentSession.completionReceivedAtMillis != null || currentSession.failed) return

        val failedSession = currentSession.copy(failed = true)
        session = failedSession
        runSession(failedSession)
    }

    /**
     * 실행 세션을 즉시 계산하고 완료 전까지 주기적으로 갱신한다.
     *
     * @param targetSession 현재 프로세스에서 이어서 표시할 생성 세션
     */
    private fun runSession(targetSession: QuizCreateSession) {
        updateJob?.cancel()
        update(targetSession)
        if (mutableState.value.status.isTerminal()) return

        updateJob =
            scope.launch {
                while (isActive) {
                    delay(
                        (
                            if (mutableState.value.status == QuizCreateStatus.Completing) {
                                COMPLETION_UPDATE_INTERVAL_MILLIS
                            } else {
                                UPDATE_INTERVAL_MILLIS
                            }
                        ).milliseconds,
                    )
                    val shouldStop =
                        mutate {
                            if (session !== targetSession) {
                                true
                            } else {
                                update(targetSession)
                                mutableState.value.status.isTerminal()
                            }
                        }
                    if (shouldStop) break
                }
            }
    }

    /**
     * 현재 시각을 기준으로 세션의 진행 상태 스냅샷을 계산한다.
     *
     * @param targetSession 진행률 계산에 사용할 시작 시각과 전체 시간을 가진 세션
     */
    private fun update(targetSession: QuizCreateSession) {
        val elapsedMillis = (nowMillis() - targetSession.startedAtMillis).coerceAtLeast(0L)
        val snapshot = quizCreateProgressSnapshot(elapsedMillis, targetSession.durationMillis)
        val completionProgress = targetSession.completionProgress(nowMillis())
        val progressPercent = completionProgress ?: snapshot.progressPercent
        val status =
            when {
                targetSession.failed -> QuizCreateStatus.Failed
                progressPercent >= 100 -> QuizCreateStatus.Completed
                completionProgress != null -> QuizCreateStatus.Completing
                else -> QuizCreateStatus.InProgress
            }
        mutableState.value =
            QuizCreateProgressState(
                projectId = targetSession.projectId,
                status = status,
                step = if (completionProgress != null) QuizCreateStep.Validation else snapshot.step,
                progressPercent = progressPercent,
            )
    }

    /**
     * 서버 완료 신호 이후 빠르게 100%까지 채울 진행률을 계산한다.
     *
     * @param currentTimeMillis 완료 애니메이션 경과 시간을 계산할 현재 Unix epoch millisecond
     * @return 완료 신호를 받았다면 100 이하의 진행률, 아직 받지 않았다면 null
     */
    private fun QuizCreateSession.completionProgress(currentTimeMillis: Long): Int? {
        val receivedAtMillis = completionReceivedAtMillis ?: return null
        val startProgress = completionStartProgress ?: return null
        val animationElapsedMillis = (currentTimeMillis - receivedAtMillis).coerceAtLeast(0L)
        val remainingProgress = 100 - startProgress
        return (
            startProgress +
                (remainingProgress * animationElapsedMillis / COMPLETION_ANIMATION_DURATION_MILLIS).toInt()
        ).coerceAtMost(100)
    }

    /** @return 상태 갱신 작업을 종료해야 하는 완료 또는 실패 상태인지 여부 */
    private fun QuizCreateStatus.isTerminal(): Boolean = this == QuizCreateStatus.Completed || this == QuizCreateStatus.Failed

    private companion object {
        const val UPDATE_INTERVAL_MILLIS = 1_000L
        const val COMPLETION_UPDATE_INTERVAL_MILLIS = 50L
        const val COMPLETION_ANIMATION_DURATION_MILLIS = 800L
        const val WAITING_PROGRESS_PERCENT = 98
    }
}

/** 현재 Unix epoch millisecond를 반환한다. */
internal expect fun currentEpochMillis(): Long
