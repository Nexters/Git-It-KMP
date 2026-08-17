package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 문제 북마크 설정 요청 본문이다.
 *
 * @property bookmarked 설정할 북마크 상태
 */
@Serializable
internal data class BookmarkQuestionRequest(
    val bookmarked: Boolean,
)

/**
 * 문제 북마크 설정 응답이다.
 *
 * @property bookmarked 서버에 적용된 북마크 상태
 */
@Serializable
internal data class BookmarkQuestionResponse(
    val bookmarked: Boolean = false,
)
