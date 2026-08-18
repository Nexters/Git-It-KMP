package com.nexters.hytime.gitit.feature.projectdetail

import com.nexters.hytime.gitit.domain.model.LearningSetSummary
import com.nexters.hytime.gitit.domain.model.ProjectDetail

/**
 * 프로젝트 상세 도메인 모델을 화면 메타 정보로 변환한다.
 *
 * @return 상세 화면 상단에 표시할 프로젝트 정보
 */
internal fun ProjectDetail.toProjectInfo(): ProjectInfo =
    ProjectInfo(
        name = repositoryName,
        repositoryUrl = repositoryUrl,
        thumbnailUrl = repositoryImageUrl,
        starCount = formatStarCount(starCount),
        techStack = techStack.joinToString(" · "),
    )

/**
 * 학습 세트 요약을 상세 화면 목록 항목으로 변환한다.
 *
 * @return 세트 라벨·제목·진행률이 채워진 목록 항목
 */
internal fun LearningSetSummary.toListItem(): LearningSetItem =
    LearningSetItem(
        id = setId,
        title = label,
        description = title,
        progress = if (problemCount > 0) completedCount * 100 / problemCount else 0,
        totalSteps = problemCount,
    )

/**
 * GitHub 스타 수를 카드 표기 문자열로 축약한다.
 *
 * @param count 스타 수
 * @return 1,000 미만은 그대로, 이상은 `3.6k`처럼 소수 한 자리 k 표기
 */
internal fun formatStarCount(count: Int): String {
    if (count < 1_000) return count.toString()
    val tenths = count / 100
    return if (tenths % 10 == 0) "${tenths / 10}k" else "${tenths / 10}.${tenths % 10}k"
}
