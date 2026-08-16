package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.RegisterProjectApiResponse
import com.nexters.hytime.gitit.data.dto.RegisterProjectRequest
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.post

/**
 * Git-it 백엔드의 프로젝트 등록 API를 호출한다.
 *
 * @property networkClient 인증과 직렬화를 적용하는 네트워크 클라이언트
 */
class ProjectRepositoryImpl(
    private val networkClient: NetworkClient,
) : ProjectRepository {
    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> =
        runCatchingResult {
            val response =
                networkClient.post<RegisterProjectRequest, RegisterProjectApiResponse>(
                    path = PATH_REGISTER_PROJECT,
                    body =
                        RegisterProjectRequest(
                            githubRepoUrl = githubRepoUrl,
                            quizLevel = quizLevel.name,
                        ),
                )
            val data =
                response.data?.takeIf { response.success }
                    ?: throw NetworkException(response.message ?: "프로젝트 등록 응답이 올바르지 않습니다.")
            data.toDomain()
        }

    private companion object {
        /** 회원의 GitHub 저장소를 학습 프로젝트로 등록하는 API 경로다. */
        private const val PATH_REGISTER_PROJECT = "/api/v1/projects"
    }
}
