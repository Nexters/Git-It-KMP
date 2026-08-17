package com.nexters.hytime.gitit.feature.bookmark

import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions

/** 전체 프로젝트를 뜻하는 필터 식별자다. */
internal const val BOOKMARK_FILTER_ALL_ID = "all"

/**
 * 북마크 도메인 모델을 저장한 문제 화면 상태로 변환한다.
 *
 * 필터는 전체 항목 뒤에 북마크가 있는 프로젝트들을 잇고, 선택 중인 필터는 유지한다.
 * 표시 문구는 화면이 문자열 리소스에서 만들므로 여기서는 값만 옮긴다.
 *
 * @param selectedFilterId 유지할 선택 필터 식별자
 * @return 필터·문제 목록이 채워진 화면 상태
 */
internal fun BookmarkedQuestions.toUiState(selectedFilterId: String): BookmarkUiState =
    BookmarkUiState(
        filters =
            listOf(BookmarkFilter(id = BOOKMARK_FILTER_ALL_ID, label = null)) +
                availableProjects.map { project -> BookmarkFilter(id = project.projectId, label = project.projectName) },
        selectedFilterId = selectedFilterId,
        questions =
            bookmarks.map { bookmark ->
                BookmarkedQuestion(
                    id = bookmark.questionId,
                    projectId = bookmark.projectId,
                    projectName = bookmark.projectName,
                    setLabel = bookmark.setLabel,
                    problemNumber = bookmark.problemNumber,
                    title = bookmark.question,
                )
            },
    )
