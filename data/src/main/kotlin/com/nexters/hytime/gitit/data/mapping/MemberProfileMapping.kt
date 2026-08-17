package com.nexters.hytime.gitit.data.mapping

import com.nexters.hytime.gitit.data.dto.DayCountResponse
import com.nexters.hytime.gitit.data.dto.MemberProfileResponse
import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DaySolvedCount
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position

/**
 * 프로필 응답을 도메인 모델로 변환한다.
 *
 * 서버가 아직 클라이언트가 모르는 개발 분야·수준을 내려주면 화면을 막지 않도록 `null`로 떨어뜨린다.
 *
 * @return 네트워크 표현을 제거한 회원 프로필
 */
internal fun MemberProfileResponse.toDomain(): MemberProfile =
    MemberProfile(
        name = name,
        email = email,
        position = position.toPosition(),
        careerLevel = careerLevel.toCareerLevel(),
        thisWeekSolvedCount = thisWeekSolvedCount,
        thisMonthSolvedCount = thisMonthSolvedCount,
        streakDays = streakDays,
        weeklyChart = weeklyChart.map(DayCountResponse::toDomain),
    )

/**
 * 요일별 문제 풀이량 응답을 도메인 모델로 변환한다.
 *
 * @return 요일 하나의 문제 풀이량
 */
internal fun DayCountResponse.toDomain(): DaySolvedCount =
    DaySolvedCount(
        dayLabel = dayLabel,
        count = count,
    )

/**
 * 서버가 내려준 이름을 개발 분야로 변환한다.
 *
 * @return 아는 값이면 해당 [Position], 값이 없거나 모르는 값이면 `null`
 */
private fun String?.toPosition(): Position? = Position.entries.firstOrNull { it.name == this }

/**
 * 서버가 내려준 이름을 개발 수준으로 변환한다.
 *
 * @return 아는 값이면 해당 [CareerLevel], 값이 없거나 모르는 값이면 `null`
 */
private fun String?.toCareerLevel(): CareerLevel? = CareerLevel.entries.firstOrNull { it.name == this }
