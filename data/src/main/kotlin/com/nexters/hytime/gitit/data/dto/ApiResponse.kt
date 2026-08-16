package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 데이터를 함께 내려주는 API의 공통 응답 봉투다.
 *
 * 서버의 `ApiResponse<T>`와 같은 모양이며, 엔드포인트마다 봉투 타입을 따로 만들지 않으려고 제네릭으로 둔다.
 * 내려줄 데이터가 없는 API는 `data` 필드 자체가 없으므로 [EmptyApiResponse]를 쓴다.
 *
 * @property success 요청 성공 여부
 * @property data 성공 시 반환되는 본문
 * @property code 실패 원인을 구분하는 서버 오류 코드
 * @property message 사용자에게 노출하지 않는 서버 오류 설명
 */
@Serializable
internal data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val code: String? = null,
    val message: String? = null,
)
