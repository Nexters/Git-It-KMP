package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 프로젝트 상세 조회 응답이다.
 *
 * @property projectId 프로젝트 식별자
 * @property repositoryUrl GitHub 저장소 링크
 * @property repositoryName 저장소 이름
 * @property repositoryImageUrl 저장소 소유자 프로필 이미지 URL
 * @property starCount GitHub 스타 수
 * @property techStack 기술 스택
 * @property overallProgressPercent 프로젝트 전체 진행률(%)
 * @property nextProblemId 이어서 풀 문제 식별자
 * @property sets 프로젝트에 속한 학습 세트 목록
 */
@Serializable
internal data class ProjectDetailResponse(
    val projectId: String,
    val repositoryUrl: String = "",
    val repositoryName: String = "",
    val repositoryImageUrl: String = "",
    val starCount: Int = 0,
    val techStack: List<String> = emptyList(),
    val overallProgressPercent: Int = 0,
    val nextProblemId: String? = null,
    val sets: List<SetResponse> = emptyList(),
)

/**
 * 프로젝트 상세에 나열되는 학습 세트 하나다.
 *
 * @property setId 학습 세트 식별자
 * @property label 세트 라벨
 * @property title 세트 제목
 * @property problemCount 세트에 속한 문제 개수
 * @property completedCount 정답을 제출한 문제 개수
 */
@Serializable
internal data class SetResponse(
    val setId: String,
    val label: String = "",
    val title: String = "",
    val problemCount: Int = 0,
    val completedCount: Int = 0,
)
