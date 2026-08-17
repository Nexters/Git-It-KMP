package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.EssayAnswerResult
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 서술형 문제의 답을 제출하고 해설과 자가채점 기준을 받는다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class SubmitEssayAnswerUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 작성한 답안을 서버에 제출한다. 다시 제출하면 이전 답을 덮어쓴다.
     *
     * @param projectId 문제가 속한 프로젝트 식별자
     * @param questionId 답을 낼 문제 식별자
     * @param text 서술형 답안. 비어 있으면 서버가 거부한다
     * @return 해설과 자가채점 기준 또는 실패 원인
     */
    suspend operator fun invoke(
        projectId: String,
        questionId: String,
        text: String,
    ): Result<EssayAnswerResult> = repository.submitEssayAnswer(projectId, questionId, text)
}
