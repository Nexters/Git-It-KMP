package com.nexters.hytime.gitit.domain.repository

import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration

/** 학습할 GitHub 저장소를 회원 프로젝트로 등록하는 계약이다. */
interface ProjectRepository {
    /**
     * GitHub 저장소를 선택한 학습 깊이로 등록한다.
     *
     * @param githubRepoUrl 서버가 조회할 공개 GitHub 저장소 URL
     * @param quizLevel 생성할 문제의 학습 깊이
     * @return 서버가 발급한 프로젝트 ID와 현재 생성 상태
     */
    suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration>
}
