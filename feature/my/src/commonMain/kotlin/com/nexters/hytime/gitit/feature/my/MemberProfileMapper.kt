package com.nexters.hytime.gitit.feature.my

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DaySolvedCount
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position

/**
 * 회원 프로필 도메인 모델을 마이 화면 상태로 변환한다.
 *
 * @return 프로필·학습 통계·주간 학습량이 채워진 화면 상태
 */
internal fun MemberProfile.toUiState(): MyUiState =
    MyUiState(
        profile =
            MyProfile(
                name = name.orEmpty(),
                email = email.orEmpty(),
                developmentField = position.toDisplayLabel(),
                learningLevel = careerLevel.toDisplayLabel(),
            ),
        stats =
            listOf(
                MyStudyStat(label = "이번 주", value = "${thisWeekSolvedCount}문제"),
                MyStudyStat(label = "이번 달", value = "${thisMonthSolvedCount}문제"),
                MyStudyStat(label = "연속 학습", value = "${streakDays}일"),
            ),
        weeklyStudy = weeklyChart.map(DaySolvedCount::toWeeklyStudy),
    )

/**
 * 요일별 문제 풀이량을 주간 차트 항목으로 변환한다.
 *
 * @return 요일 라벨과 풀이 수를 담은 차트 항목
 */
private fun DaySolvedCount.toWeeklyStudy(): MyWeeklyStudy =
    MyWeeklyStudy(
        day = dayLabel,
        solvedCount = count,
    )

/**
 * 개발 분야를 화면 표기 라벨로 변환한다.
 *
 * @return 표기 라벨. 큐레이션 전이라 값이 없으면 빈 문자열
 */
internal fun Position?.toDisplayLabel(): String =
    when (this) {
        Position.BACKEND -> "Back-end"
        Position.FRONTEND -> "Front-end"
        Position.IOS -> "iOS"
        Position.ANDROID -> "Android"
        null -> ""
    }

/**
 * 개발 수준을 화면 표기 라벨로 변환한다.
 *
 * @return 표기 라벨. 큐레이션 전이라 값이 없으면 빈 문자열
 */
internal fun CareerLevel?.toDisplayLabel(): String =
    when (this) {
        CareerLevel.ENTRY -> "입문"
        CareerLevel.JUNIOR -> "주니어"
        CareerLevel.MIDDLE -> "미들"
        CareerLevel.SENIOR -> "시니어"
        null -> ""
    }
