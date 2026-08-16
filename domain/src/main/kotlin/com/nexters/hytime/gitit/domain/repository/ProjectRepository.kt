package com.nexters.hytime.gitit.domain.repository

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

    companion object {
        /** 서버 기본값과 같은 첫 페이지 번호다. */
        const val DEFAULT_PAGE = 0

        /** 서버 기본값과 같은 페이지 크기다. */
        const val DEFAULT_PAGE_SIZE = 10
    }
}
