package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 북마크한 문제 목록을 조회한다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class GetBookmarkedQuestionsUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 저장한 문제 화면에 표시할 북마크 목록을 가져온다.
     *
     * @param projectId 특정 프로젝트로 좁힐 때 사용할 식별자. `null`이면 전체를 조회한다
     * @return 조회된 북마크 목록 또는 실패 원인
     */
    suspend operator fun invoke(projectId: String? = null): Result<BookmarkedQuestions> = repository.getBookmarkedQuestions(projectId)
}
