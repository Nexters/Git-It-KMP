package com.nexters.hytime.gitit.data.mapping

import com.nexters.hytime.gitit.data.dto.ProjectItemResponse
import com.nexters.hytime.gitit.data.dto.ProjectListResponse
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectSummary

/**
 * 프로젝트 목록 응답을 도메인 페이지로 변환한다.
 *
 * @return 네트워크 표현을 제거한 프로젝트 한 페이지
 */
internal fun ProjectListResponse.toDomain(): ProjectPage =
    ProjectPage(
        items = items.map(ProjectItemResponse::toDomain),
        hasNext = hasNext,
    )

/**
 * 목록 항목 응답을 도메인 요약으로 변환한다.
 *
 * @return 목록 화면이 사용할 프로젝트 요약
 */
internal fun ProjectItemResponse.toDomain(): ProjectSummary =
    ProjectSummary(
        projectId = projectId,
        repositoryName = repositoryName,
        repositoryImageUrl = repositoryImageUrl,
        techStack = techStack,
        currentSetLabel = currentSetLabel,
        currentSetTitle = currentSetTitle,
        nextProblemId = nextProblemId,
        overallProgressPercent = overallProgressPercent,
    )
