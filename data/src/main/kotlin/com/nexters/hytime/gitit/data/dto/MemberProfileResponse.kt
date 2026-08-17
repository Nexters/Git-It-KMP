package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 마이 화면에 필요한 프로필과 학습 현황이다.
 *
 * 개발 분야와 개발 수준은 서버가 값을 추가해도 역직렬화가 깨지지 않도록 문자열로 받고,
 * 도메인 변환 단계에서 아는 값만 열거형으로 바꾼다.
 *
 * @property name 회원 이름. 큐레이션 전이면 `null`
 * @property email 소셜 계정 이메일
 * @property position 개발 분야 열거형 이름
 * @property careerLevel 개발 수준 열거형 이름
 * @property thisWeekSolvedCount 이번 주 푼 문제 수
 * @property thisMonthSolvedCount 이번 달 푼 문제 수
 * @property streakDays 연속 학습 일수
 * @property weeklyChart 이번 주 요일별 문제 풀이량
 */
@Serializable
internal data class MemberProfileResponse(
    val name: String? = null,
    val email: String? = null,
    val position: String? = null,
    val careerLevel: String? = null,
    val thisWeekSolvedCount: Int = 0,
    val thisMonthSolvedCount: Int = 0,
    val streakDays: Int = 0,
    val weeklyChart: List<DayCountResponse> = emptyList(),
)

/**
 * 요일 하나의 문제 풀이량이다.
 *
 * @property dayLabel 서버가 내려주는 요일 표기
 * @property count 해당 요일에 푼 문제 수
 */
@Serializable
internal data class DayCountResponse(
    val dayLabel: String,
    val count: Int,
)
