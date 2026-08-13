package com.nexters.hytime.gitit.presentation.app

/**
 * 앱 최상위 화면의 인증 상태다.
 *
 * @property isSignedIn 저장된 로그인 세션이 있는지 여부
 */
data class AppUiState(
    val isSignedIn: Boolean = false,
)
