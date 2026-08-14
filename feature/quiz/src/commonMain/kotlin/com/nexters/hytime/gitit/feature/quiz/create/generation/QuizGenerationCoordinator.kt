package com.nexters.hytime.gitit.feature.quiz.create.generation

import com.nexters.hytime.gitit.feature.quiz.create.QuizGenerationStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * 화면 이동과 무관하게 유지할 문제 생성 세션 저장소다.
 */
interface QuizGenerationSessionStore {
    /** 저장된 생성 세션을 반환한다. */
    fun load(): QuizGenerationSession?

    /**
     * 생성 세션을 저장한다.
     *
     * @param session 앱 재실행 후에도 복원할 세션
     */
    fun save(session: QuizGenerationSession)

    /** 저장된 생성 세션을 제거한다. */
    fun clear()
}

/**
 * 문제 생성 진행률을 계산하기 위한 영속 세션이다.
 *
 * @property projectId 생성 대상 프로젝트 식별자
 * @property startedAtMillis 생성 시작 시각의 Unix epoch millisecond
 * @property durationMillis 0%에서 100%까지 도달할 전체 시간
 * @property completionReceivedAtMillis 서버 완료 신호를 받은 시각. 아직 받지 않았다면 null
 * @property completionStartProgress 완료 애니메이션을 시작한 진행률. 아직 시작하지 않았다면 null
 * @property failed 서버 실패 신호를 받아 생성이 중단되었는지 여부
 */
data class QuizGenerationSession(
    val projectId: String,
    val startedAtMillis: Long,
    val durationMillis: Long,
    val completionReceivedAtMillis: Long? = null,
    val completionStartProgress: Int? = null,
    val failed: Boolean = false,
)

/** 문제 생성 세션의 현재 실행 상태다. */
enum class QuizGenerationStatus {
    /** 진행 중인 생성 세션이 없다. */
    Idle,

    /** 표시용 생성 타임라인이 진행 중이다. */
    Generating,

    /** 서버 완료 신호를 받아 진행률을 빠르게 100%로 채우고 있다. */
    Completing,

    /** 표시용 생성 타임라인이 완료되었다. */
    Completed,

    /** 서버가 생성 실패를 전달해 사용자 선택을 기다리고 있다. */
    Failed,
}

/**
 * 앱 전체에서 공유할 문제 생성 진행 상태다.
 *
 * @property projectId 생성 대상 프로젝트 식별자
 * @property status 생성 세션 상태
 * @property step 현재 화면에 표시할 생성 단계
 * @property progressPercent 0..100 범위의 전체 진행률
 * @property isHomeModalVisible 홈 화면 진행 모달 노출 여부
 */
data class QuizGenerationState(
    val projectId: String? = null,
    val status: QuizGenerationStatus = QuizGenerationStatus.Idle,
    val step: QuizGenerationStep = QuizGenerationStep.ProjectInfo,
    val progressPercent: Int = 0,
    val isHomeModalVisible: Boolean = false,
)

/**
 * 시작 시각을 기준으로 문제 생성 진행 상태를 계산하고 여러 화면에 공유한다.
 *
 * @param store 프로세스 재시작 후 세션을 복원할 저장소
 * @param nowMillis 현재 Unix epoch millisecond를 반환하는 함수
 * @param scope 화면 생명주기와 독립적으로 진행 상태를 갱신할 앱 범위 코루틴 스코프
 * @param durationMillisProvider 새 생성 세션의 전체 시간을 결정하는 함수
 */
class QuizGenerationCoordinator(
    private val store: QuizGenerationSessionStore,
    private val nowMillis: () -> Long = ::currentEpochMillis,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val durationMillisProvider: () -> Long = LocalQuizGenerator::randomDurationMillis,
) {
    private val _state = MutableStateFlow(QuizGenerationState())

    /** 홈과 문제 생성 화면이 함께 구독할 현재 상태다. */
    val state: StateFlow<QuizGenerationState> = _state.asStateFlow()

    private var updateJob: Job? = null

    init {
        store.load()?.let { session -> resume(session, showHomeModal = true) }
    }

    /**
     * 기존 세션을 대체하고 새 문제 생성 타임라인을 시작한다.
     *
     * @param projectId 생성 대상 프로젝트 식별자
     */
    fun start(projectId: String) {
        val session =
            QuizGenerationSession(
                projectId = projectId,
                startedAtMillis = nowMillis(),
                durationMillis = durationMillisProvider(),
            )
        store.save(session)
        resume(session, showHomeModal = false)
    }

    /**
     * FCM 데이터로 전달된 문제 생성 완료 신호를 현재 세션에 반영한다.
     *
     * 진행 중인 프로젝트와 식별자가 다르거나 이미 완료된 경우에는 무시한다.
     *
     * @param projectId 완료된 프로젝트 식별자
     */
    fun complete(projectId: String) {
        val currentSession = store.load() ?: return
        if (currentSession.projectId != projectId || currentSession.completionReceivedAtMillis != null || currentSession.failed) return

        val completedSession =
            currentSession.copy(
                completionReceivedAtMillis = nowMillis(),
                completionStartProgress = _state.value.progressPercent.coerceIn(0, WAITING_PROGRESS_PERCENT),
            )
        store.save(completedSession)
        resume(completedSession, showHomeModal = _state.value.isHomeModalVisible)
    }

    /**
     * 서버가 전달한 문제 생성 실패 신호를 현재 세션에 반영한다.
     *
     * 진행 중인 프로젝트와 식별자가 다르거나 이미 종료된 세션이면 무시한다.
     *
     * @param projectId 생성에 실패한 프로젝트 식별자
     */
    fun fail(projectId: String) {
        val currentSession = store.load() ?: return
        if (currentSession.projectId != projectId || currentSession.completionReceivedAtMillis != null || currentSession.failed) return

        val failedSession = currentSession.copy(failed = true)
        store.save(failedSession)
        resume(failedSession, showHomeModal = _state.value.isHomeModalVisible)
    }

    /** 홈 화면에서 진행 모달을 표시한다. */
    fun showHomeModal() {
        if (_state.value.status != QuizGenerationStatus.Idle) {
            _state.value = _state.value.copy(isHomeModalVisible = true)
        }
    }

    /** 생성 세션은 유지하면서 홈 진행 모달만 닫는다. */
    fun hideHomeModal() {
        _state.value = _state.value.copy(isHomeModalVisible = false)
    }

    /** 현재 생성 세션과 저장된 복원 정보를 모두 제거한다. */
    fun cancel() {
        updateJob?.cancel()
        updateJob = null
        store.clear()
        _state.value = QuizGenerationState()
    }

    /**
     * 저장된 세션을 즉시 계산하고 완료 전까지 주기적으로 갱신한다.
     *
     * @param session 이어서 표시할 생성 세션
     * @param showHomeModal 복원 직후 홈 진행 모달을 표시할지 여부
     */
    private fun resume(
        session: QuizGenerationSession,
        showHomeModal: Boolean,
    ) {
        updateJob?.cancel()
        update(session)
        if (showHomeModal) {
            _state.value = _state.value.copy(isHomeModalVisible = true)
        }
        if (_state.value.status == QuizGenerationStatus.Completed || _state.value.status == QuizGenerationStatus.Failed) return

        updateJob =
            scope.launch {
                while (isActive) {
                    delay(
                        (
                            if (_state.value.status == QuizGenerationStatus.Completing) {
                                COMPLETION_UPDATE_INTERVAL_MILLIS
                            } else {
                                UPDATE_INTERVAL_MILLIS
                            }
                        ).milliseconds,
                    )
                    update(session)
                    if (
                        _state.value.status == QuizGenerationStatus.Completed ||
                        _state.value.status == QuizGenerationStatus.Failed
                    ) {
                        break
                    }
                }
            }
    }

    /**
     * 현재 시각을 기준으로 세션의 진행 상태 스냅샷을 계산한다.
     *
     * @param session 진행률 계산에 사용할 시작 시각과 전체 시간을 가진 세션
     */
    private fun update(session: QuizGenerationSession) {
        val elapsedMillis = (nowMillis() - session.startedAtMillis).coerceAtLeast(0L)
        val snapshot = quizGenerationProgressSnapshot(elapsedMillis, session.durationMillis)
        val completionProgress = session.completionProgress(nowMillis())
        val progressPercent = completionProgress ?: snapshot.progressPercent
        val status =
            when {
                session.failed -> QuizGenerationStatus.Failed
                progressPercent >= 100 -> QuizGenerationStatus.Completed
                completionProgress != null -> QuizGenerationStatus.Completing
                else -> QuizGenerationStatus.Generating
            }
        _state.value =
            QuizGenerationState(
                projectId = session.projectId,
                status = status,
                step = if (completionProgress != null) QuizGenerationStep.Validation else snapshot.step,
                progressPercent = progressPercent,
                isHomeModalVisible = _state.value.isHomeModalVisible,
            )
    }

    /**
     * 서버 완료 신호 이후 빠르게 100%까지 채울 진행률을 계산한다.
     *
     * @param currentTimeMillis 완료 애니메이션 경과 시간을 계산할 현재 Unix epoch millisecond
     * @return 완료 신호를 받았다면 100 이하의 진행률, 아직 받지 않았다면 null
     */
    private fun QuizGenerationSession.completionProgress(currentTimeMillis: Long): Int? {
        val receivedAtMillis = completionReceivedAtMillis ?: return null
        val startProgress = completionStartProgress ?: return null
        val animationElapsedMillis = (currentTimeMillis - receivedAtMillis).coerceAtLeast(0L)
        val remainingProgress = 100 - startProgress
        return (
            startProgress +
                (remainingProgress * animationElapsedMillis / COMPLETION_ANIMATION_DURATION_MILLIS).toInt()
        ).coerceAtMost(100)
    }

    private companion object {
        const val UPDATE_INTERVAL_MILLIS = 1_000L
        const val COMPLETION_UPDATE_INTERVAL_MILLIS = 50L
        const val COMPLETION_ANIMATION_DURATION_MILLIS = 800L
        const val WAITING_PROGRESS_PERCENT = 98
    }
}

/** 현재 Unix epoch millisecond를 반환한다. */
internal expect fun currentEpochMillis(): Long
