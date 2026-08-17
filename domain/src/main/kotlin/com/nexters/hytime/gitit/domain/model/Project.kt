package com.nexters.hytime.gitit.domain.model

/**
 * 프로젝트 목록 한 페이지다.
 *
 * @property items 이 페이지에 담긴 프로젝트
 * @property hasNext 다음 페이지가 남아 있는지 여부
 */
data class ProjectPage(
    val items: List<ProjectSummary>,
    val hasNext: Boolean,
)

/**
 * 목록 화면에 표시할 프로젝트 요약이다.
 *
 * @property projectId 프로젝트 식별자
 * @property repositoryName GitHub 저장소 이름
 * @property repositoryImageUrl 저장소 소유자 프로필 이미지 URL
 * @property techStack 저장소에서 추출한 기술 스택
 * @property currentSetLabel 다음에 풀 문제가 속한 세트 라벨 (예: `"Set 1"`)
 * @property currentSetTitle 다음에 풀 문제가 속한 세트 제목
 * @property nextSetId 이어서 풀 문제가 속한 학습 세트 식별자. 풀 문제가 없으면 `null`
 * @property nextQuestionId 이어서 풀 문제 식별자. 풀 문제가 없으면 `null`
 * @property overallProgressPercent 프로젝트 전체 진행률(%)
 */
data class ProjectSummary(
    val projectId: String,
    val repositoryName: String,
    val repositoryImageUrl: String,
    val techStack: List<String>,
    val currentSetLabel: String,
    val currentSetTitle: String,
    val nextSetId: String?,
    val nextQuestionId: String?,
    val overallProgressPercent: Int,
)

/**
 * 프로젝트 상세 화면에 표시할 정보다.
 *
 * @property projectId 프로젝트 식별자
 * @property repositoryUrl GitHub 저장소 링크
 * @property repositoryName 저장소 이름
 * @property repositoryImageUrl 저장소 소유자 프로필 이미지 URL
 * @property starCount GitHub 스타 수
 * @property techStack 저장소에서 추출한 기술 스택
 * @property overallProgressPercent 프로젝트 전체 진행률(%)
 * @property nextProblemId 이어서 풀 문제 식별자. 풀 문제가 없으면 `null`
 * @property sets 프로젝트에 속한 학습 세트 목록
 */
data class ProjectDetail(
    val projectId: String,
    val repositoryUrl: String,
    val repositoryName: String,
    val repositoryImageUrl: String,
    val starCount: Int,
    val techStack: List<String>,
    val overallProgressPercent: Int,
    val nextProblemId: String?,
    val sets: List<LearningSetSummary>,
)

/**
 * 프로젝트 상세에 나열되는 학습 세트 요약이다.
 *
 * @property setId 학습 세트 식별자
 * @property label 세트 라벨 (예: `"Set 1"`)
 * @property title 세트 제목
 * @property problemCount 세트에 속한 문제 개수
 * @property completedCount 정답을 제출한 문제 개수
 */
data class LearningSetSummary(
    val setId: String,
    val label: String,
    val title: String,
    val problemCount: Int,
    val completedCount: Int,
)
