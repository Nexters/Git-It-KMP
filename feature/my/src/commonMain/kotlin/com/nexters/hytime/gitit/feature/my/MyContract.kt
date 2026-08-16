package com.nexters.hytime.gitit.feature.my

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
 * @property developmentField 사용자가 선택한 개발 분야
 * @property learningLevel 사용자가 선택한 학습 수준
 */
data class MyProfile(
    val name: String = "",
    val email: String = "",
    val developmentField: String = "",
    val learningLevel: String = "",
)

/**
 * 학습 현황 요약 항목이다.
 *
 * @property label 항목 이름
 * @property value 항목 값
 */
data class MyStudyStat(
    val label: String,
    val value: String,
)

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
