package com.nexters.hytime.gitit.feature.projectlist

import com.nexters.hytime.gitit.domain.model.ProjectSummary

/**
 * 프로젝트 요약 도메인 모델을 목록 카드 모델로 변환한다.
 *
 * @return 목록 화면에 표시할 프로젝트 카드
 */
internal fun ProjectSummary.toListItem(): ProjectListItem =
    ProjectListItem(
        id = projectId,
        title = repositoryName,
        thumbnailUrl = repositoryImageUrl,
        techStack = techStack.joinToString(" · "),
        setLabel = currentSetLabel,
        recentSetTitle = currentSetTitle,
        progress = overallProgressPercent,
    )
