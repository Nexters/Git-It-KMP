package com.nexters.hytime.gitit.feature.home

import com.nexters.hytime.gitit.domain.model.ProjectSummary

/**
 * 프로젝트 요약 도메인 모델을 홈 학습 카드 모델로 변환한다.
 *
 * @return 홈 화면에 표시할 학습 카드
 */
internal fun ProjectSummary.toLearningProject(): HomeLearningProject =
    HomeLearningProject(
        id = projectId,
        title = repositoryName,
        technologies = techStack.joinToString(" · "),
        setLabel = currentSetLabel,
        description = currentSetTitle,
        progress = overallProgressPercent / 100f,
    )
