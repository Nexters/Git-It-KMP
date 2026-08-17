package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 응답 본문 없이 성공 여부만 반환하는 API 응답이다.
 *
 * 서버는 내려줄 데이터가 없을 때 `data` 필드를 아예 담지 않으므로 이 타입에는 `data`가 없다.
 *
 * @property success 요청 성공 여부
 * @property code 실패 원인을 구분하는 서버 오류 코드
 * @property message 사용자에게 노출하지 않는 서버 오류 설명
 */
@Serializable
internal data class EmptyApiResponse(
    val success: Boolean,
    val code: String? = null,
    val message: String? = null,
)
