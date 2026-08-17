package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 4지선다 문제의 답을 제출하고 채점 결과를 받는다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class SubmitChoiceAnswerUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 고른 선택지를 서버에 제출한다. 다시 제출하면 이전 답을 덮어쓴다.
     *
     * @param projectId 문제가 속한 프로젝트 식별자
     * @param questionId 답을 낼 문제 식별자
     * @param selectedIndex 고른 선택지 번호. 0부터 시작한다
     * @return 정답 여부·정답 번호·해설 또는 실패 원인
     */
    suspend operator fun invoke(
        projectId: String,
        questionId: String,
        selectedIndex: Int,
    ): Result<ChoiceAnswerResult> = repository.submitChoiceAnswer(projectId, questionId, selectedIndex)
}
