package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 북마크한 문제 목록 조회 응답이다.
 *
 * @property totalCount 필터를 적용한 뒤의 북마크 총 개수
 * @property availableProjects 북마크가 하나라도 있는 프로젝트 전부
 * @property bookmarks 북마크한 문제 목록
 */
@Serializable
internal data class BookmarkedQuestionListResponse(
    val totalCount: Int = 0,
    val availableProjects: List<AvailableProjectResponse> = emptyList(),
    val bookmarks: List<BookmarkedQuestionResponse> = emptyList(),
)

/**
 * 북마크 필터에 노출할 프로젝트다.
 *
 * @property projectId 프로젝트 식별자
 * @property projectName 저장소 이름
 */
@Serializable
internal data class AvailableProjectResponse(
    val projectId: String,
    val projectName: String = "",
)

/**
 * 북마크한 문제 하나다.
 *
 * @property projectId 문제가 속한 프로젝트 식별자
 * @property projectName 저장소 이름
 * @property setLabel 세트 라벨
 * @property problemNumber 세트 내 문제 번호
 * @property questionId 문제 식별자
 * @property question 문제 본문
 */
@Serializable
internal data class BookmarkedQuestionResponse(
    val projectId: String,
    val projectName: String = "",
    val setLabel: String = "",
    val problemNumber: Int = 0,
    val questionId: String,
    val question: String = "",
)
