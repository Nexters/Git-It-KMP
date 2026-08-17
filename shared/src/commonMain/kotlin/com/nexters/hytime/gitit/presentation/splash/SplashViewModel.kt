package com.nexters.hytime.gitit.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.repository.AuthRepository
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 앱 시작 시 저장된 액세스 토큰을 검증하고 첫 화면을 결정한다.
 *
 * @property authRepository 액세스 토큰을 백엔드에서 검증하는 저장소
 * @property sessionStorage 플랫폼에 저장된 로그인 세션을 제공하는 저장소
 */
class SplashViewModel(
    private val authRepository: AuthRepository,
    private val sessionStorage: LoginSessionStorage,
) : ViewModel() {
    /** 토큰 검증 실패 원인을 기록하는 로거다. */
    private val logger = gitItLogger()

    /** 화면에 제공할 변경 가능한 내부 상태다. */
    private val _uiState = MutableStateFlow(SplashUiState())

    /** 외부에서 읽기만 가능한 스플래시 상태다. */
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    /** 초기 화면 이동을 한 번 보관하는 내부 채널이다. */
    private val sideEffectChannel = Channel<SplashSideEffect>(Channel.BUFFERED)

    /** Route에서 구독할 첫 화면 이동 부작용이다. */
    val sideEffects: Flow<SplashSideEffect> = sideEffectChannel.receiveAsFlow()

    init {
        verifyAccessToken()
    }

    /** 저장된 세션과 서버 검증 결과에 따라 첫 화면 이동을 요청한다. */
    private fun verifyAccessToken() {
        viewModelScope.launch {
            val sideEffect = resolveDestination()
            _uiState.value = SplashUiState(isCheckingToken = false)
            sideEffectChannel.send(sideEffect)
        }
    }

    /**
     * 저장된 세션과 액세스 토큰 검증 결과로 이동할 화면을 결정한다.
     *
     * @return 토큰이 유효하면 홈, 그 외에는 온보딩 이동 부작용
     */
    private suspend fun resolveDestination(): SplashSideEffect =
        try {
            if (sessionStorage.load() == null) {
                SplashSideEffect.NavigateToOnboarding
            } else {
                authRepository
                    .verifyAccessToken()
                    .fold(
                        onSuccess = { SplashSideEffect.NavigateToHome },
                        onFailure = { error ->
                            logger.e(throwable = error) { "저장된 액세스 토큰 검증 실패" }
                            SplashSideEffect.NavigateToOnboarding
                        },
                    )
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            logger.e(throwable = error) { "저장된 로그인 세션 확인 실패" }
            SplashSideEffect.NavigateToOnboarding
        }
}
