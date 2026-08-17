package com.nexters.hytime.gitit.domain.model

/**
 * 문제 풀이 화면이 사용하는 학습 세트 하나다.
 *
 * 정답·해설·채점 기준은 담기지 않는다. 제출 응답이 돌려주므로, 여기 실으면 풀기 전에 정답이 클라이언트에 놓인다.
 *
 * @property setId 학습 세트 식별자
 * @property title 세트 제목
 * @property description 세트 설명
 * @property orientation 문제를 풀기 전에 읽는 안내
 * @property level 이 프로젝트에 걸린 난이도. 아래 문제는 전부 이 레벨이다
 * @property questions 만들어진 순서 그대로의 문제 목록. 이미 푼 문제도 걸러내지 않는다
 */
data class LearningSet(
    val setId: String,
    val title: String,
    val description: String,
    val orientation: String,
    val level: ProjectQuizLevel?,
    val questions: List<Question>,
)

/**
 * 학습 세트에 담긴 문제 하나다.
 *
 * 풀었는지는 [myAnswer]가 있는지로 판단한다. 별도 플래그를 두면 둘이 어긋날 수 있다.
 *
 * @property questionId 답을 제출할 때 사용할 문제 식별자
 * @property format 문제 형식. 어느 제출 API를 부를지를 결정한다
 * @property text 문제 본문
 * @property choices 선택지. 서술형이면 빈 목록
 * @property sources 이 문제가 인용한 코드 위치
 * @property myAnswer 이미 푼 문제라면 그때 낸 답, 아니면 `null`
 */
data class Question(
    val questionId: String,
    val format: QuestionFormat?,
    val text: String,
    val choices: List<String>,
    val sources: List<QuestionSource>,
    val myAnswer: MyAnswer?,
)

/**
 * 문제가 인용한 코드 위치다.
 *
 * @property file 저장소 루트 기준 상대 경로
 * @property startLine 인용한 첫 줄
 * @property endLine 인용한 마지막 줄
 * @property symbol 그 범위에 적혀 있던 식별자
 * @property summary 이 자리가 무엇을 하는 곳인지. 짝지어 둔 설명이 없으면 `null`
 * @property url GitHub에서 이 코드를 여는 주소. 커밋으로 고정돼 있다
 */
data class QuestionSource(
    val file: String,
    val startLine: Int,
    val endLine: Int,
    val symbol: String,
    val summary: String?,
    val url: String,
)

/**
 * 이미 제출한 답이다.
 *
 * 채워지는 값은 문제 형식을 따라간다 — 4지선다면 [selectedIndex]·[correct], 서술형이면 [text]다.
 *
 * @property selectedIndex 고른 선택지 번호(0부터). 서술형이면 `null`
 * @property text 제출한 답안. 4지선다면 `null`
 * @property correct 정답 여부. 서술형은 학습자가 스스로 채점하므로 항상 `null`
 * @property answeredAt 제출 시각. 서버가 준 ISO-8601 문자열 그대로 보관한다
 */
data class MyAnswer(
    val selectedIndex: Int?,
    val text: String?,
    val correct: Boolean?,
    val answeredAt: String,
)
