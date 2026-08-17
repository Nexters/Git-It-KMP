package com.nexters.hytime.gitit.feature.bookmark

/**
 * 저장한 문제 화면의 단일 UI 상태다.
 *
 * @property filters 프로젝트 필터 목록
 * @property selectedFilterId 선택된 필터 식별자
 * @property questions 저장한 문제 목록
 * @property bookmarkChanges 사용자가 변경한 문제별 북마크 상태
 */
data class BookmarkUiState(
    val filters: List<BookmarkFilter> = emptyList(),
    val selectedFilterId: String = "",
    val questions: List<BookmarkedQuestion> = emptyList(),
    val bookmarkChanges: Map<String, Boolean> = emptyMap(),
)

/**
 * 저장한 문제 필터 항목이다.
 *
 * @property id 필터 식별자
 * @property label 화면에 표시할 필터 이름
 */
data class BookmarkFilter(
    val id: String,
    val label: String,
)

/**
 * 저장한 문제 카드 한 개를 표현한다.
 *
 * @property id 문제 식별자
 * @property projectId 문제가 속한 프로젝트 식별자
 * @property meta 프로젝트·세트·문제 번호 정보
 * @property title 문제 제목
 */
data class BookmarkedQuestion(
    val id: String,
    val projectId: String = "",
    val meta: String,
    val title: String,
)

/**
 * 저장한 문제 화면에서 발생하는 사용자 의도다.
 */
sealed interface BookmarkIntent {
    /** 홈 탭 선택. */
    data object HomeTabClick : BookmarkIntent

    /** 프로젝트 탭 선택. */
    data object ProjectTabClick : BookmarkIntent

    /** 저장 탭 선택. 현재 화면이므로 이동하지 않는다. */
    data object SavedTabClick : BookmarkIntent

    /** 마이 탭 선택. */
    data object MyTabClick : BookmarkIntent

    /**
     * 필터 선택.
     *
     * @property filterId 선택한 필터 식별자
     */
    data class FilterClick(
        val filterId: String,
    ) : BookmarkIntent

    /**
     * 북마크 토글 선택.
     *
     * @property questionId 선택한 문제 식별자
     */
    data class BookmarkClick(
        val questionId: String,
    ) : BookmarkIntent

    /**
     * 문제 풀기 선택.
     *
     * @property questionId 선택한 문제 식별자
     */
    data class SolveClick(
        val questionId: String,
    ) : BookmarkIntent
}

/**
 * 저장한 문제 화면이 한 번만 전달해야 하는 이벤트다.
 */
sealed interface BookmarkSideEffect {
    /** 홈 화면으로 이동. */
    data object NavigateToHome : BookmarkSideEffect

    /** 프로젝트 리스트 화면으로 이동. */
    data object NavigateToProjectList : BookmarkSideEffect

    /** 마이 화면으로 이동. */
    data object NavigateToMy : BookmarkSideEffect
}
