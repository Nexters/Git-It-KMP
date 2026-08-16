package com.nexters.hytime.gitit.domain.model

/**
 * 저장한 문제 화면에 표시할 북마크 목록이다.
 *
 * @property totalCount 필터를 적용한 뒤의 북마크 총 개수
 * @property availableProjects 북마크가 하나라도 있는 프로젝트 전부. 프로젝트 필터와 무관하게 항상 전체 목록이다
 * @property bookmarks 북마크한 문제 목록
 */
data class BookmarkedQuestions(
    val totalCount: Int,
    val availableProjects: List<AvailableProject>,
    val bookmarks: List<BookmarkedQuestion>,
)

/**
 * 북마크 필터에 노출할 프로젝트다.
 *
 * @property projectId 프로젝트 식별자
 * @property projectName 저장소 이름
 */
data class AvailableProject(
    val projectId: String,
    val projectName: String,
)

/**
 * 북마크한 문제 하나다.
 *
 * @property projectId 문제가 속한 프로젝트 식별자
 * @property projectName 저장소 이름
 * @property setLabel 세트 라벨 (예: `"Set 1"`)
 * @property problemNumber 세트 내 문제 번호. 1부터 시작한다
 * @property questionId 문제 식별자
 * @property question 문제 본문
 */
data class BookmarkedQuestion(
    val projectId: String,
    val projectName: String,
    val setLabel: String,
    val problemNumber: Int,
    val questionId: String,
    val question: String,
)
