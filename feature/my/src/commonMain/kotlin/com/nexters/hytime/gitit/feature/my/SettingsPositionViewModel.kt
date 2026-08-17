package com.nexters.hytime.gitit.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.usecase.GetMemberProfileUseCase
import com.nexters.hytime.gitit.domain.usecase.UpdatePositionUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 개발 분야 선택 화면의 단일 UI 상태다.
 *
 * @property selected 현재 선택한 개발 분야. 프로필 조회 전이거나 큐레이션 전이면 null
 */
data class SettingsPositionUiState(
    val selected: Position? = null,
)

/** 개발 분야 선택 화면이 한 번만 전달해야 하는 이벤트다. */
sealed interface SettingsPositionSideEffect {
    /** 이전 화면으로 이동. */
    data object NavigateBack : SettingsPositionSideEffect
}

/**
 * 개발 분야 선택과 저장을 관리한다.
 *
 * 디자인 주석대로 카드 선택은 화면 안에서만 반영하고, 저장은 뒤로가기에서 수행한 뒤 이전 화면으로 이동한다.
 *
 * @property getMemberProfile 현재 선택된 개발 분야를 알기 위한 프로필 조회 유스케이스
 * @property updatePosition 개발 분야를 서버에 저장하는 유스케이스
 */
class SettingsPositionViewModel(
    private val getMemberProfile: GetMemberProfileUseCase,
    private val updatePosition: UpdatePositionUseCase,
) : ViewModel() {
    private val logger = gitItLogger()

    /** 화면 진입 시점의 개발 분야다. 값이 그대로면 저장 요청을 보내지 않는다. */
    private var initialPosition: Position? = null

    private val _uiState = MutableStateFlow(SettingsPositionUiState())

    /** 개발 분야 선택 화면이 구독할 현재 UI 상태이다. */
    val uiState: StateFlow<SettingsPositionUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<SettingsPositionSideEffect>(extraBufferCapacity = 1)

    /** 개발 분야 선택 화면에서 한 번만 처리할 이벤트 스트림이다. */
    val sideEffects: SharedFlow<SettingsPositionSideEffect> = _sideEffects.asSharedFlow()

    init {
        loadCurrentPosition()
    }

    /**
     * 카드를 눌러 개발 분야를 선택한다. 서버 저장은 뒤로가기에서 일어난다.
     *
     * @param position 새로 선택한 개발 분야
     */
    fun onPositionClick(position: Position) {
        _uiState.value = SettingsPositionUiState(selected = position)
    }

    /**
     * 선택이 바뀌었으면 서버에 저장하고 이전 화면으로 이동한다.
     * 저장이 실패해도 화면은 이동하며 원인을 로그로 남긴다.
     */
    fun onBackClick() {
        viewModelScope.launch {
            val selected = _uiState.value.selected
            if (selected != null && selected != initialPosition) {
                updatePosition(selected)
                    .onFailure { error -> logger.e(throwable = error) { "개발 분야 변경 실패" } }
            }
            _sideEffects.emit(SettingsPositionSideEffect.NavigateBack)
        }
    }

    /**
     * 프로필을 조회해 현재 개발 분야를 선택 상태로 표시한다.
     * 실패하면 선택 없이 시작하고 원인을 로그로 남긴다.
     */
    private fun loadCurrentPosition() {
        viewModelScope.launch {
            getMemberProfile()
                .onSuccess { profile ->
                    initialPosition = profile.position
                    _uiState.value = SettingsPositionUiState(selected = profile.position)
                }.onFailure { error -> logger.e(throwable = error) { "개발 분야 조회 실패" } }
        }
    }
}
