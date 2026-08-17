package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 설정 화면에서 개발 분야만 변경하는 요청 본문이다.
 *
 * @property position 개발 분야 열거형 이름
 */
@Serializable
internal data class PositionRequest(
    val position: String,
)
