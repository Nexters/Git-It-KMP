package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 서버에 저장된 프로젝트 문제 생성 상태를 조회한다.
 *
 * @property repository 프로젝트 생성 상태를 제공하는 저장소
 */
class GetProjectGenerationStatusUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 홈과 알림에서 사용할 최신 생성 상태를 가져온다.
     *
     * @param projectId 생성 상태를 확인할 프로젝트 식별자
     * @return 최신 생성 상태 또는 조회 실패
     */
    suspend operator fun invoke(projectId: String): Result<ProjectGenerationStatus> = repository.getProjectGenerationStatus(projectId)
}
