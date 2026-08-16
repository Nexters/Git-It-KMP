package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.ApiResponse
import com.nexters.hytime.gitit.data.dto.ProjectDetailResponse
import com.nexters.hytime.gitit.data.dto.ProjectListResponse
import com.nexters.hytime.gitit.data.dto.RegisterProjectRequest
import com.nexters.hytime.gitit.data.dto.RegisterProjectResponse
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.get
import com.nexters.hytime.gitit.network.api.post

/**
 * 백엔드 Project API를 호출하는 저장소 구현체다.
 *
 * @property networkClient HTTP 구현을 숨긴 네트워크 클라이언트
 */
class ProjectRepositoryImpl(
    private val networkClient: NetworkClient,
) : ProjectRepository {
    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> =
        runCatchingResult {
            networkClient
                .post<RegisterProjectRequest, ApiResponse<RegisterProjectResponse>>(
                    PATH_PROJECTS,
                    RegisterProjectRequest(
                        githubRepoUrl = githubRepoUrl,
                        quizLevel = quizLevel.name,
                    ),
                ).requireData("프로젝트 등록 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun getProjects(
        page: Int,
        size: Int,
    ): Result<ProjectPage> =
        runCatchingResult {
            require(page >= 0) { "페이지 번호는 0 이상이어야 합니다." }
            require(size > 0) { "페이지 크기는 1 이상이어야 합니다." }
            networkClient
                .get<ApiResponse<ProjectListResponse>>(
                    PATH_PROJECTS,
                    queryParameters = mapOf("page" to page.toString(), "size" to size.toString()),
                ).requireData("프로젝트 목록 조회 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun getProjectDetail(projectId: String): Result<ProjectDetail> =
        runCatchingResult {
            requireProjectId(projectId)
            networkClient
                .get<ApiResponse<ProjectDetailResponse>>("$PATH_PROJECTS/$projectId")
                .requireData("프로젝트 상세 조회 응답이 올바르지 않습니다.")
                .toDomain()
        }

    /**
     * 프로젝트 식별자가 경로에 넣을 수 있는 값인지 확인한다.
     *
     * @param projectId 검사할 프로젝트 식별자
     * @throws IllegalArgumentException 값이 비어 있는 경우
     */
    private fun requireProjectId(projectId: String) {
        require(projectId.isNotBlank()) { "프로젝트 식별자가 비어 있습니다." }
    }

    private companion object {
        private const val PATH_PROJECTS = "/api/v1/projects"
    }
}
