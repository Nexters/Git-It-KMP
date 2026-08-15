package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 프로젝트 등록 요청 본문이다.
 *
 * @property githubRepoUrl 등록할 공개 GitHub 저장소 URL
 * @property quizLevel 문제의 학습 깊이를 나타내는 L1~L3 값
 */
@Serializable
internal data class RegisterProjectRequest(
    val githubRepoUrl: String,
    val quizLevel: String,
)

/**
 * 프로젝트 등록 API의 공통 응답이다.
 *
 * @property success 요청 성공 여부
 * @property data 성공 시 반환되는 프로젝트 정보
 * @property code 실패 원인을 구분하는 서버 오류 코드
 * @property message 실패 원인을 설명하는 서버 메시지
 */
@Serializable
internal data class RegisterProjectApiResponse(
    val success: Boolean,
    val data: RegisterProjectResponse? = null,
    val code: String? = null,
    val message: String? = null,
)

/**
 * 서버가 반환한 프로젝트 등록 정보다.
 *
 * @property projectId 프로젝트 상세와 FCM 결과를 식별하는 ID
 * @property status 서버 문제 저장소의 현재 생성 상태
 */
@Serializable
internal data class RegisterProjectResponse(
    val projectId: String,
    val status: String,
)
