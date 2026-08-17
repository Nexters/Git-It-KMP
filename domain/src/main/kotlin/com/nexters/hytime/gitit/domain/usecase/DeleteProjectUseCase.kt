package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 등록한 프로젝트를 삭제한다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class DeleteProjectUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 프로젝트를 서버에서 삭제한다.
     *
     * @param projectId 삭제할 프로젝트 식별자
     * @return 삭제 결과 또는 실패 원인
     */
    suspend operator fun invoke(projectId: String): Result<Unit> = repository.deleteProject(projectId)
}
