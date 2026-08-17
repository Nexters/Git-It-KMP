package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 등록한 프로젝트 목록을 페이지 단위로 조회한다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class GetProjectsUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 프로젝트 한 페이지를 가져온다.
     *
     * @param page 0부터 시작하는 페이지 번호
     * @param size 한 페이지에 담을 프로젝트 수
     * @return 조회된 페이지 또는 실패 원인
     */
    suspend operator fun invoke(
        page: Int = ProjectRepository.DEFAULT_PAGE,
        size: Int = ProjectRepository.DEFAULT_PAGE_SIZE,
    ): Result<ProjectPage> = repository.getProjects(page, size)
}
