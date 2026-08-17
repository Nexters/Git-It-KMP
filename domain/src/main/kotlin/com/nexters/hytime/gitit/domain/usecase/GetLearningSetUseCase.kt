package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 문제 풀이에 필요한 학습 세트를 조회한다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class GetLearningSetUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 학습 세트 하나를 문제 목록과 함께 가져온다.
     *
     * @param projectId 세트가 속한 프로젝트 식별자
     * @param setId 조회할 학습 세트 식별자
     * @return 조회된 학습 세트 또는 실패 원인
     */
    suspend operator fun invoke(
        projectId: String,
        setId: String,
    ): Result<LearningSet> = repository.getLearningSet(projectId, setId)
}
