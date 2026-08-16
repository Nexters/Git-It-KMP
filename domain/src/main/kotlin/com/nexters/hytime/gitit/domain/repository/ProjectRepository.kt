package com.nexters.hytime.gitit.domain.repository

import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration

/**
 * 백엔드 Project API에 대응하는 도메인 리포지토리 계약이다.
 *
 * 모든 함수가 현재 로그인 세션의 회원을 대상으로 하므로 회원 식별자를 받지 않는다.
 */
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

    /**
     * 등록한 프로젝트 목록을 페이지 단위로 조회한다.
     *
     * @param page 0부터 시작하는 페이지 번호
     * @param size 한 페이지에 담을 프로젝트 수
     * @return 조회 결과. 성공 시 프로젝트 한 페이지, 실패 시 예외를 담는다
     */
    suspend fun getProjects(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_PAGE_SIZE,
    ): Result<ProjectPage>

    /**
     * 프로젝트 상세 정보를 조회한다.
     *
     * @param projectId 조회할 프로젝트 식별자
     * @return 조회 결과. 성공 시 프로젝트 상세, 실패 시 예외를 담는다
     */
    suspend fun getProjectDetail(projectId: String): Result<ProjectDetail>

    /**
     * 프로젝트를 삭제한다.
     *
     * @param projectId 삭제할 프로젝트 식별자
     * @return 삭제 결과. 성공 시 [Unit], 실패 시 예외를 담는다
     */
    suspend fun deleteProject(projectId: String): Result<Unit>

    /**
     * 문제 풀이에 필요한 학습 세트를 조회한다.
     *
     * @param projectId 세트가 속한 프로젝트 식별자
     * @param setId 조회할 학습 세트 식별자
     * @return 조회 결과. 성공 시 학습 세트, 실패 시 예외를 담는다
     */
    suspend fun getLearningSet(
        projectId: String,
        setId: String,
    ): Result<LearningSet>

    companion object {
        /** 서버 기본값과 같은 첫 페이지 번호다. */
        const val DEFAULT_PAGE = 0

        /** 서버 기본값과 같은 페이지 크기다. */
        const val DEFAULT_PAGE_SIZE = 10
    }
}
