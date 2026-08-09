package com.nexters.hytime.gitit.feature.home

/**
 * 홈 화면이 표시할 상태를 정의한다.
 *
 * @property isContentReady 홈의 실제 콘텐츠를 표시할 준비가 완료됐는지 여부
 */
data class HomeUiState(
    val isContentReady: Boolean = false,
)

/**
 * 사용자가 홈 화면에서 발생시킨 의도를 정의한다.
 */
sealed interface HomeIntent {
    /**
     * 홈 콘텐츠를 새로고침하도록 요청한다.
     */
    data object Refresh : HomeIntent

    /**
     * 홈 탭을 선택한다.
     */
    data object HomeTabClick : HomeIntent

    /**
     * 프로젝트 탭을 선택한다.
     */
    data object ProjectTabClick : HomeIntent

    /**
     * 저장 탭을 선택한다.
     */
    data object SavedTabClick : HomeIntent

    /**
     * 마이 탭을 선택한다.
     */
    data object MyTabClick : HomeIntent
}

/**
 * 홈 화면이 한 번만 전달해야 하는 이벤트를 정의한다.
 */
sealed interface HomeSideEffect {
    /**
     * 프로젝트 리스트 화면으로 이동한다.
     */
    data object NavigateToProjectList : HomeSideEffect

    /**
     * 마이 화면으로 이동한다.
     */
    data object NavigateToMy : HomeSideEffect

    /**
     * 저장한 문제 화면으로 이동한다.
     */
    data object NavigateToBookmark : HomeSideEffect
}
