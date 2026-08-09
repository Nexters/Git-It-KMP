package com.nexters.hytime.gitit.presentation.onboarding

import com.nexters.hytime.gitit.domain.model.Account

/**
 * 온보딩 화면의 UI 상태다.
 *
 * 구글 로그인 진행 상황에 따라 버튼 활성화와 화면 전환을 결정한다.
 */
sealed interface OnboardingUiState {
    /** 로그인을 아직 시도하지 않은 초기 상태다. */
    data object Idle : OnboardingUiState

    /** 로그인 진행 중이다. 중복 요청을 막기 위해 버튼을 비활성화한다. */
    data object Loading : OnboardingUiState

    /** 로그인에 성공해 계정 정보를 보유한 상태다. 홈 화면으로 이동한다.
     *
     * @property account 인증된 계정
     */
    data class Success(
        val account: Account,
    ) : OnboardingUiState

    /** 로그인에 실패한 상태다.
     *
     * @property message 사용자에게 보여줄 오류 메시지
     */
    data class Error(
        val message: String,
    ) : OnboardingUiState
}
