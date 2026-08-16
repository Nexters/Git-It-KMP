package com.nexters.hytime.gitit.data.mapping

import com.nexters.hytime.gitit.data.dto.RegisterProjectResponse
import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.model.ProjectRegistration

/**
 * 프로젝트 등록 응답을 앱 도메인 모델로 변환한다.
 *
 * @return 검증된 프로젝트 ID와 생성 상태
 * @throws IllegalArgumentException 프로젝트 ID가 비었거나 서버 상태가 지원 범위를 벗어난 경우
 */
internal fun RegisterProjectResponse.toDomain(): ProjectRegistration {
    require(projectId.isNotBlank()) { "프로젝트 ID가 비어 있습니다." }
    val generationStatus =
        when (status) {
            "READY" -> ProjectGenerationStatus.Ready
            "ANCHORED" -> ProjectGenerationStatus.Anchored
            "FAILED" -> ProjectGenerationStatus.Failed
            "COMPLETED" -> ProjectGenerationStatus.Completed
            else -> throw IllegalArgumentException("지원하지 않는 프로젝트 생성 상태입니다: $status")
        }
    return ProjectRegistration(
        projectId = projectId,
        status = generationStatus,
    )
}
