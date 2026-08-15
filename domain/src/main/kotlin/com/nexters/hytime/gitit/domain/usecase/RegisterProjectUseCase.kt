package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult

/**
 * 학습 설정을 백엔드 프로젝트로 등록한다.
 *
 * @property repository 프로젝트 등록을 수행하는 도메인 계약
 */
class RegisterProjectUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 유효한 GitHub URL과 학습 깊이를 서버에 전달한다.
     *
     * @param githubRepoUrl 공개 GitHub 저장소의 HTTPS URL
     * @param quizLevel 사용자가 선택한 문제 학습 깊이
     * @return 등록된 프로젝트 또는 실패 원인
     */
    suspend operator fun invoke(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> =
        runCatchingResult {
            require(githubRepoUrl.isNotBlank()) { "GitHub 저장소 URL이 비어 있습니다." }
            repository.registerProject(githubRepoUrl.trim(), quizLevel).getOrThrow()
        }
}
