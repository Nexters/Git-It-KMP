package com.nexters.hytime.gitit.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.usecase.SignInUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Google 로그인 화면의 상태를 관리하는 ViewModel이다.
 *
 * [SignInUseCase]를 호출해 ID Token 획득부터 백엔드 검증까지 수행하고,
 * 결과를 [SignInUiState]로 노출한다. UseCase가 이미 [kotlin.coroutines.cancellation.CancellationException]을
 * [Result] 밖으로 재던지므로 여기서는 성공/실패만 다룬다.
 *
 * @property signInUseCase 로그인 전체 흐름을 수행하는 유스케이스
 */
class SignInViewModel(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    /**
     * 로그인 진행 상태. 외부에는 읽기 전용 [kotlinx.coroutines.flow.StateFlow]로 노출된다.
     */
    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    /**
     * 로그인을 수행한다. 상태를 [SignInUiState.Loading]으로 바꾼 뒤 유스케이스를 실행한다.
     */
    fun signIn() {
        _uiState.value = SignInUiState.Loading
        viewModelScope.launch {
            signInUseCase()
                .onSuccess { session -> _uiState.value = SignInUiState.Success(session) }
                .onFailure { error ->
                    logger.e(throwable = error) { "로그인 실패" }
                    _uiState.value = SignInUiState.Error(error.message ?: "알 수 없는 오류")
                }
        }
    }
}
