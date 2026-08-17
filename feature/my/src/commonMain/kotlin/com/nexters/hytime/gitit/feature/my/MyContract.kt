package com.nexters.hytime.gitit.feature.my

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.Position

/**
 * 마이 화면의 단일 UI 상태다.
 *
 * @property profile 사용자 프로필 정보
 * @property stats 학습 누적 수치
 * @property weeklyStudy 요일별 학습량
 */
data class MyUiState(
    val profile: MyProfile = MyProfile(),
    val stats: List<MyStudyStat> = emptyList(),
    val weeklyStudy: List<MyWeeklyStudy> = emptyList(),
)

/**
 * 마이 화면 상단 프로필 정보다.
 *
 * @property name 화면에 표시할 사용자 이름
 * @property email 사용자 계정 이메일
 * @property position 사용자가 선택한 개발 분야. 큐레이션 전이면 null
 * @property careerLevel 사용자가 선택한 학습 수준. 큐레이션 전이면 null
 */
data class MyProfile(
    val name: String = "",
    val email: String = "",
    val position: Position? = null,
    val careerLevel: CareerLevel? = null,
)

/**
 * 학습 현황 요약 항목이다.
 *
 * 항목 이름과 값의 단위 표기는 [type]에 맞춰 화면이 만든다.
 *
 * @property type 항목 종류
 * @property count 항목이 나타내는 수치
 */
data class MyStudyStat(
    val type: MyStudyStatType,
    val count: Int,
)

/** 학습 현황 요약 항목의 종류다. */
enum class MyStudyStatType {
    /** 이번 주에 푼 문제 수. */
    THIS_WEEK,

    /** 이번 달에 푼 문제 수. */
    THIS_MONTH,

    /** 연속으로 학습한 일수. */
    STREAK,
}

/**
 * 주간 학습량 막대 한 개를 표현한다.
 *
 * @property day 표시할 요일
 * @property solvedCount 해당 요일에 학습한 문제 수
 */
data class MyWeeklyStudy(
    val day: String,
    val solvedCount: Int,
)

/**
 * 마이 화면에서 발생하는 사용자 의도다.
 */
sealed interface MyIntent {
    /** 설정 화면 진입. */
    data object SettingsClick : MyIntent

    /** 홈 탭 선택. */
    data object HomeTabClick : MyIntent

    /** 프로젝트 탭 선택. */
    data object ProjectTabClick : MyIntent

    /** 저장 탭 선택. */
    data object SavedTabClick : MyIntent

    /** 마이 탭 선택. 현재 화면이므로 이동하지 않는다. */
    data object MyTabClick : MyIntent
}

/**
 * 마이 화면이 한 번만 전달해야 하는 이벤트다.
 */
sealed interface MySideEffect {
    /** 설정 화면으로 이동. */
    data object NavigateToSettings : MySideEffect

    /** 홈 화면으로 이동. */
    data object NavigateToHome : MySideEffect

    /** 프로젝트 리스트 화면으로 이동. */
    data object NavigateToProjectList : MySideEffect

    /** 저장한 문제 화면으로 이동. */
    data object NavigateToBookmark : MySideEffect
}
