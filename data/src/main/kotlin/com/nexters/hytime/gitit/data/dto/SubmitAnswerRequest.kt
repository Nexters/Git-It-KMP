package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 4지선다 답 제출 요청 본문이다.
 *
 * @property selectedIndex 고른 선택지 번호. 문제의 선택지 순서를 따르는 0부터 시작하는 번호다
 */
@Serializable
internal data class SubmitChoiceAnswerRequest(
    val selectedIndex: Int,
)

/**
 * 4지선다 답 제출 응답이다.
 *
 * @property questionId 답을 낸 문제 식별자
 * @property correct 고른 선택지가 정답인지 여부
 * @property answerIndex 정답 선택지 번호
 * @property explanation 해설
 */
@Serializable
internal data class SubmitChoiceAnswerResponse(
    val questionId: String,
    val correct: Boolean = false,
    val answerIndex: Int = 0,
    val explanation: String = "",
)
