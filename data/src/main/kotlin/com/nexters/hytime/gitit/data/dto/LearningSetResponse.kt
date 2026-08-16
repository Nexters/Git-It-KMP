package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 학습 세트 조회 응답이다.
 *
 * 난이도와 문제 형식은 서버가 값을 추가해도 역직렬화가 깨지지 않도록 문자열로 받고,
 * 도메인 변환 단계에서 아는 값만 열거형으로 바꾼다.
 *
 * @property setId 학습 세트 식별자
 * @property title 세트 제목
 * @property description 세트 설명
 * @property orientation 문제를 풀기 전에 읽는 안내
 * @property level 난이도 열거형 이름
 * @property questions 세트에 속한 문제 목록
 */
@Serializable
internal data class LearningSetResponse(
    val setId: String,
    val title: String = "",
    val description: String = "",
    val orientation: String = "",
    val level: String? = null,
    val questions: List<QuestionResponse> = emptyList(),
)

/**
 * 학습 세트에 담긴 문제 하나다.
 *
 * @property questionId 문제 식별자
 * @property format 문제 형식 열거형 이름
 * @property text 문제 본문
 * @property choices 선택지. 서술형이면 빈 목록
 * @property sources 문제가 인용한 코드 위치
 * @property myAnswer 이미 푼 문제라면 그때 낸 답
 */
@Serializable
internal data class QuestionResponse(
    val questionId: String,
    val format: String? = null,
    val text: String = "",
    val choices: List<String> = emptyList(),
    val sources: List<SourceResponse> = emptyList(),
    val myAnswer: MyAnswerResponse? = null,
)

/**
 * 문제가 인용한 코드 위치다.
 *
 * @property file 저장소 루트 기준 상대 경로
 * @property startLine 인용한 첫 줄
 * @property endLine 인용한 마지막 줄
 * @property symbol 그 범위에 적혀 있던 식별자
 * @property summary 이 자리가 무엇을 하는 곳인지
 * @property url GitHub에서 이 코드를 여는 주소
 */
@Serializable
internal data class SourceResponse(
    val file: String = "",
    val startLine: Int = 0,
    val endLine: Int = 0,
    val symbol: String = "",
    val summary: String? = null,
    val url: String = "",
)

/**
 * 이미 제출한 답이다.
 *
 * @property selectedIndex 고른 선택지 번호. 서술형이면 `null`
 * @property text 제출한 답안. 4지선다면 `null`
 * @property correct 정답 여부. 서술형이면 `null`
 * @property answeredAt 제출 시각. ISO-8601 문자열이다
 */
@Serializable
internal data class MyAnswerResponse(
    val selectedIndex: Int? = null,
    val text: String? = null,
    val correct: Boolean? = null,
    val answeredAt: String = "",
)
