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
 * 서술형 답 제출 요청 본문이다.
 *
 * @property text 서술형 답안
 */
@Serializable
internal data class SubmitEssayAnswerRequest(
    val text: String,
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

/**
 * 서술형 답 제출 응답이다.
 *
 * @property questionId 답을 낸 문제 식별자
 * @property explanation 해설
 * @property rubric 자가채점 기준
 */
@Serializable
internal data class SubmitEssayAnswerResponse(
    val questionId: String,
    val explanation: String = "",
    val rubric: RubricResponse = RubricResponse(),
)

/**
 * 서술형 자가채점 기준이다.
 *
 * @property criteria 판단 기준별 배점
 * @property keyPoints 답안에 들어가야 할 핵심
 * @property fullMarkExample 만점 답안 예시
 * @property partialExample 부분 점수 답안 예시
 * @property zeroExample 0점 답안 예시
 */
@Serializable
internal data class RubricResponse(
    val criteria: List<RubricCriterionResponse> = emptyList(),
    val keyPoints: List<String> = emptyList(),
    val fullMarkExample: String = "",
    val partialExample: String = "",
    val zeroExample: String = "",
)

/**
 * 채점 기준 하나다.
 *
 * @property text 판단 기준
 * @property points 이 기준의 배점
 */
@Serializable
internal data class RubricCriterionResponse(
    val text: String = "",
    val points: Int = 0,
)
