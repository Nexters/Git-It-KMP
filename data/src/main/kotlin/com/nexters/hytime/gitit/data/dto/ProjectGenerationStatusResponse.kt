package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 프로젝트 문제 생성 상태 조회 응답이다.
 *
 * @property status 서버의 READY, STARTED, REJECTED, FAILED, COMPLETED 상태
 */
@Serializable
internal data class ProjectGenerationStatusResponse(
    val status: String,
)
