package com.nexters.hytime.gitit.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.usecase.GetMemberProfileUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 설정 화면의 단일 UI 상태다.
 *
 * 표기 라벨은 화면이 문자열 리소스에서 만들므로 여기서는 선택 값만 들고 있다.
 *
 * @property position 현재 선택한 개발 분야. 큐레이션 전이면 null
 * @property careerLevel 현재 선택한 개발 수준. 큐레이션 전이면 null
 */
data class SettingsUiState(
    val position: Position? = null,
    val careerLevel: CareerLevel? = null,
)

/**
 * 설정 화면에 표시할 회원의 현재 학습 설정을 관리한다.
 *
 * @property getMemberProfile 회원 프로필을 조회하는 유스케이스
 */
class SettingsViewModel(
    private val getMemberProfile: GetMemberProfileUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    private val _uiState = MutableStateFlow(SettingsUiState())

    /** 설정 화면이 구독할 현재 UI 상태이다. */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /**
     * 회원 프로필을 조회해 현재 개발 분야·수준 라벨을 갱신한다.
     *
     * 선택 화면에서 값을 바꾸고 돌아온 경우를 반영해야 하므로 화면에 진입할 때마다 호출한다.
     * 실패하면 이전 값을 유지하고 원인을 로그로 남긴다.
     */
    fun refresh() {
        viewModelScope.launch {
            getMemberProfile()
                .onSuccess { profile ->
                    _uiState.value =
                        SettingsUiState(
                            position = profile.position,
                            careerLevel = profile.careerLevel,
                        )
                }.onFailure { error -> logger.e(throwable = error) { "설정 프로필 조회 실패" } }
        }
    }
}
