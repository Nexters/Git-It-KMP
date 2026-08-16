package com.nexters.hytime.gitit.domain.model

/**
 * 서술형 답을 제출한 결과다.
 *
 * 채점자가 학습자 자신이라 정답 여부 대신 스스로 대조할 [rubric]이 온다.
 *
 * @property questionId 답을 낸 문제 식별자
 * @property explanation 해설
 * @property rubric 자가채점 기준
 */
data class EssayAnswerResult(
    val questionId: String,
    val explanation: String,
    val rubric: Rubric,
)

/**
 * 서술형 자가채점 기준이다.
 *
 * @property criteria 판단 기준별 배점. 합이 만점이다
 * @property keyPoints 답안에 들어가야 할 핵심
 * @property fullMarkExample 만점 답안 예시
 * @property partialExample 부분 점수 답안 예시
 * @property zeroExample 0점 답안 예시
 */
data class Rubric(
    val criteria: List<RubricCriterion>,
    val keyPoints: List<String>,
    val fullMarkExample: String,
    val partialExample: String,
    val zeroExample: String,
)

/**
 * 채점 기준 하나다. 정답 나열이 아니라 판단 기준을 담는다.
 *
 * @property text 판단 기준 (예: `"실제 파일명을 들어 설명했는가"`)
 * @property points 이 기준의 배점
 */
data class RubricCriterion(
    val text: String,
    val points: Int,
)
