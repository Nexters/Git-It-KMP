package com.nexters.hytime.gitit.feature.quiz.create.session

import com.nexters.hytime.gitit.domain.usecase.RegisterProjectUseCase
import com.nexters.hytime.gitit.domain.util.runCatchingResult

/**
 * 실패한 문제 생성 세션을 동일한 저장소와 학습 깊이로 서버에 다시 등록한다.
 *
 * @property registerProject 프로젝트 등록 API를 호출하는 UseCase
 * @property createStore 재시도 입력과 새 프로젝트 생성 상태를 공유하는 Store
 */
class QuizCreateRetryHandler(
    private val registerProject: RegisterProjectUseCase,
    private val createStore: QuizCreateStore,
) {
    /**
     * 현재 실패 세션의 프로젝트 등록 API를 다시 호출한다.
     *
     * @return 새 프로젝트 생성 세션을 시작했으면 성공, 요청 불가 또는 API 실패면 실패
     */
    suspend fun retry(): Result<Unit> =
        runCatchingResult {
            val request = createStore.beginRetry() ?: error("재시도할 문제 생성 요청이 없습니다.")
            registerProject(
                githubRepoUrl = request.repositoryUrl,
                quizLevel = request.quizLevel,
            ).onSuccess { registration ->
                createStore.start(registration, request)
            }.onFailure {
                createStore.retryFailed()
            }.getOrThrow()
        }
}
