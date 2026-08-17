package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 프로젝트 목록 조회 응답이다.
 *
 * @property items 이 페이지에 담긴 프로젝트
 * @property hasNext 다음 페이지 존재 여부
 */
@Serializable
internal data class ProjectListResponse(
    val items: List<ProjectItemResponse> = emptyList(),
    val hasNext: Boolean = false,
)

/**
 * 목록에 표시할 프로젝트 하나다.
 *
 * @property projectId 프로젝트 식별자
 * @property repositoryName 저장소 이름
 * @property repositoryImageUrl 저장소 소유자 프로필 이미지 URL
 * @property techStack 기술 스택
 * @property currentSetLabel 다음에 풀 문제가 속한 세트 라벨
 * @property currentSetTitle 다음에 풀 문제가 속한 세트 제목
 * @property nextSetId 이어서 풀 문제가 속한 학습 세트 식별자
 * @property nextQuestionId 이어서 풀 문제 식별자
 * @property overallProgressPercent 프로젝트 전체 진행률(%)
 */
@Serializable
internal data class ProjectItemResponse(
    val projectId: String,
    val repositoryName: String = "",
    val repositoryImageUrl: String = "",
    val techStack: List<String> = emptyList(),
    val currentSetLabel: String = "",
    val currentSetTitle: String = "",
    val nextSetId: String? = null,
    val nextQuestionId: String? = null,
    val overallProgressPercent: Int = 0,
)
