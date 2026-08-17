package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 프로젝트 상세 정보를 조회한다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class GetProjectDetailUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 상세 화면에 표시할 프로젝트 정보를 가져온다.
     *
     * @param projectId 조회할 프로젝트 식별자
     * @return 조회된 상세 정보 또는 실패 원인
     */
    suspend operator fun invoke(projectId: String): Result<ProjectDetail> = repository.getProjectDetail(projectId)
}
