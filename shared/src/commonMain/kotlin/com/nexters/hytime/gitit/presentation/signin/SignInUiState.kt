package com.nexters.hytime.gitit.presentation.signin

import com.nexters.hytime.gitit.domain.model.LoginSession

/**
 * Google 로그인 화면의 UI 상태다.
 *
 * 화면은 이 상태에 따라 버튼 활성화, 로딩 표시, 결과 메시지를 렌더한다.
 */
sealed interface SignInUiState {
    /** 로그인을 아직 시도하지 않은 초기 상태다. */
    data object Idle : SignInUiState

    /** 로그인 진행 중이다. 중복 요청을 막기 위해 버튼을 비활성화한다. */
    data object Loading : SignInUiState

    /** 로그인에 성공해 계정 정보를 보유한 상태다.
     *
     * @property session 백엔드에서 발급받은 로그인 세션
     */
    data class Success(
        val session: LoginSession,
    ) : SignInUiState

    /** 로그인에 실패한 상태다.
     *
     * @property message 사용자에게 보여줄 오류 메시지
     */
    data class Error(
        val message: String,
    ) : SignInUiState
}
