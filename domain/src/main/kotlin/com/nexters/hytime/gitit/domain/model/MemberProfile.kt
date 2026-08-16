package com.nexters.hytime.gitit.domain.model

/**
 * 마이 화면에 표시할 내 프로필과 학습 현황이다.
 *
 * 큐레이션을 마치기 전에는 [name], [position], [careerLevel]이 아직 없을 수 있다.
 *
 * @property name 회원 이름. 큐레이션 전이면 `null`
 * @property email 소셜 계정 이메일. 제공되지 않은 계정이면 `null`
 * @property position 선택한 개발 분야. 큐레이션 전이면 `null`
 * @property careerLevel 선택한 개발 수준. 큐레이션 전이면 `null`
 * @property thisWeekSolvedCount 이번 주 월요일부터 오늘까지 푼 문제 수
 * @property thisMonthSolvedCount 이번 달 1일부터 오늘까지 푼 문제 수
 * @property streakDays 연속 학습 일수. 오늘 아직 풀지 않았어도 어제까지 이어져 있으면 유지된다
 * @property weeklyChart 이번 주 요일별 문제 풀이량. 월요일부터 일요일까지 7개다
 */
data class MemberProfile(
    val name: String?,
    val email: String?,
    val position: Position?,
    val careerLevel: CareerLevel?,
    val thisWeekSolvedCount: Int,
    val thisMonthSolvedCount: Int,
    val streakDays: Int,
    val weeklyChart: List<DaySolvedCount>,
)

/**
 * 하루 동안 푼 문제 수다.
 *
 * @property dayLabel 서버가 내려주는 요일 표기 (예: `"월"`)
 * @property count 해당 요일에 푼 문제 수
 */
data class DaySolvedCount(
    val dayLabel: String,
    val count: Int,
)
