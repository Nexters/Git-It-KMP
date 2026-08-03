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
}

/**
 * 홈 화면이 한 번만 전달해야 하는 이벤트를 정의한다.
 */
sealed interface HomeSideEffect
