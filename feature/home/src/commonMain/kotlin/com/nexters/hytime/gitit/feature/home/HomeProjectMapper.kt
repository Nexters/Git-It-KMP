package com.nexters.hytime.gitit.feature.home

import com.nexters.hytime.gitit.domain.model.CareerLevel
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

/**
 * 개발 수준을 홈 프로필 헤더의 역할 표기로 변환한다.
 *
 * 시안의 표기(Junior Developer) 패턴을 수준별로 잇는다.
 *
 * @return 역할 표기. 큐레이션 전이라 값이 없으면 빈 문자열
 */
internal fun CareerLevel?.toRoleLabel(): String =
    when (this) {
        CareerLevel.ENTRY -> "Entry Developer"
        CareerLevel.JUNIOR -> "Junior Developer"
        CareerLevel.MIDDLE -> "Middle Developer"
        CareerLevel.SENIOR -> "Senior Developer"
        null -> ""
    }
