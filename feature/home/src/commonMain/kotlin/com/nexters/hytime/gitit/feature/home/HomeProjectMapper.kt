package com.nexters.hytime.gitit.feature.home

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.ProjectSummary
import git_it_kmp.feature.home.generated.resources.Res
import git_it_kmp.feature.home.generated.resources.home_role_entry
import git_it_kmp.feature.home.generated.resources.home_role_junior
import git_it_kmp.feature.home.generated.resources.home_role_middle
import git_it_kmp.feature.home.generated.resources.home_role_senior
import org.jetbrains.compose.resources.StringResource

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
 * 개발 수준을 홈 프로필 헤더의 역할 표기 문구 리소스로 변환한다.
 *
 * 시안의 표기(Junior Developer) 패턴을 수준별로 잇는다.
 *
 * @return 이름 아래에 표시할 역할 표기 리소스
 */
internal fun CareerLevel.toRoleLabelResource(): StringResource =
    when (this) {
        CareerLevel.ENTRY -> Res.string.home_role_entry
        CareerLevel.JUNIOR -> Res.string.home_role_junior
        CareerLevel.MIDDLE -> Res.string.home_role_middle
        CareerLevel.SENIOR -> Res.string.home_role_senior
    }
