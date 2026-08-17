package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.repository.ProjectRepository

/**
 * 문제의 북마크 상태를 서버에 설정한다.
 *
 * @property repository 프로젝트 정보를 제공하는 도메인 계약
 */
class BookmarkQuestionUseCase(
    private val repository: ProjectRepository,
) {
    /**
     * 북마크 상태를 설정하고 서버에 적용된 값을 받는다.
     *
     * @param projectId 문제가 속한 프로젝트 식별자
     * @param questionId 북마크할 문제 식별자
     * @param bookmarked 설정할 북마크 상태
     * @return 서버에 적용된 북마크 상태 또는 실패 원인
     */
    suspend operator fun invoke(
        projectId: String,
        questionId: String,
        bookmarked: Boolean,
    ): Result<Boolean> = repository.bookmarkQuestion(projectId, questionId, bookmarked)
}
