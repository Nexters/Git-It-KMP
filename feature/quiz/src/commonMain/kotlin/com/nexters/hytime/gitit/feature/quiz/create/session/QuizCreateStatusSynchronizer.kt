package com.nexters.hytime.gitit.feature.quiz.create.session

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.usecase.GetProjectGenerationStatusUseCase

/**
 * 저장된 프로젝트의 서버 생성 상태를 앱 범위 Store에 반영한다.
 *
 * @property getProjectGenerationStatus 서버의 최신 생성 상태를 조회하는 유스케이스
 * @property createStore 홈과 생성 화면이 공유하는 상태 Store
 * @property storage 프로세스 종료 뒤에도 대기 중 프로젝트를 복원하는 저장소
 */
class QuizCreateStatusSynchronizer(
    private val getProjectGenerationStatus: GetProjectGenerationStatusUseCase,
    private val createStore: QuizCreateStore,
    private val storage: PendingQuizCreationStorage,
) {
    /** @return 저장된 프로젝트가 있으면 동기화한 결과, 없으면 성공 */
    suspend fun syncPending(): Result<Unit> = storage.projectId?.let { sync(it) } ?: Result.success(Unit)

    /**
     * 지정한 프로젝트가 현재 대기 중인 작업이면 서버 상태를 조회해 반영한다.
     *
     * @param projectId FCM 또는 저장소에서 받은 프로젝트 식별자
     * @return 조회와 상태 반영 결과. 현재 작업과 관계없는 식별자는 성공으로 무시한다
     */
    suspend fun sync(projectId: String): Result<Unit> {
        val isCurrent = storage.projectId == projectId || createStore.state.value.projectId == projectId
        if (!isCurrent) return Result.success(Unit)

        return getProjectGenerationStatus(projectId).mapCatching { status ->
            createStore.updateFromServer(projectId, status)
        }
    }

    /**
     * 상태 API 호출이 실패했을 때 FCM payload의 terminal 상태를 대신 반영한다.
     *
     * @param projectId 결과가 도착한 프로젝트 식별자
     * @param status FCM payload가 전달한 완료·실패·거절 상태
     */
    suspend fun applyFallback(
        projectId: String,
        status: ProjectGenerationStatus,
    ) {
        createStore.updateFromServer(projectId, status)
    }
}
