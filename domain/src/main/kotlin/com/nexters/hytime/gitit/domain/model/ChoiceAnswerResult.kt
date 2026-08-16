package com.nexters.hytime.gitit.domain.model

/**
 * 4지선다 답을 제출한 결과다.
 *
 * @property questionId 답을 낸 문제 식별자
 * @property correct 고른 선택지가 정답인지 여부
 * @property answerIndex 정답 선택지 번호(0부터). 틀렸을 때 정답을 바로 보여줄 수 있다
 * @property explanation 해설. 선택지를 번호가 아니라 내용으로 가리킨다
 */
data class ChoiceAnswerResult(
    val questionId: String,
    val correct: Boolean,
    val answerIndex: Int,
    val explanation: String,
)
