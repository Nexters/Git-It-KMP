package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 온보딩에서 받은 회원 큐레이션 정보를 저장하는 요청 본문이다.
 *
 * @property name 회원이 입력한 이름
 * @property position 개발 분야 열거형 이름
 * @property careerLevel 개발 수준 열거형 이름
 */
@Serializable
internal data class CurationRequest(
    val name: String,
    val position: String,
    val careerLevel: String,
)
