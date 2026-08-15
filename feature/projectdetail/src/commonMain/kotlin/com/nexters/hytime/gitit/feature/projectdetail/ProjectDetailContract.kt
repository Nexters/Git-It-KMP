package com.nexters.hytime.gitit.feature.projectdetail

/**
 * 프로젝트 상세 화면의 단일 UI 상태(MVI State)다.
 *
 * 도메인 모델이 아직 없으므로 presentation 레이어 임시 모델([ProjectInfo], [LearningSetItem])로
 * 채운다.
 *
 * TODO: domain/data 연동 시 도메인 모델로 교체한다.
 *
 * @property project 프로젝트 메타 정보. null이면 로딩 중
 * @property learningSets 학습 세트 목록
 * @property totalProgress 전체 학습 진행률(0..100)
 * @property showMoreMenu 더보기 메뉴 노출 여부
 */
data class ProjectDetailUiState(
    val project: ProjectInfo? = null,
    val learningSets: List<LearningSetItem> = emptyList(),
    val totalProgress: Int = 0,
    val showMoreMenu: Boolean = false,
)

/**
 * 프로젝트 메타 정보를 담는 임시 presentation 모델이다.
 *
 * @property name 프로젝트 이름
 * @property thumbnailUrl 썸네일 이미지 URL
 * @property starCount GitHub 스타 수를 축약해 표시할 문자열
 * @property techStack 기술 스택 요약 문자열 (예: "Kotlin · Compose · Coroutines")
 */
data class ProjectInfo(
    val name: String,
    val thumbnailUrl: String,
    val starCount: String,
    val techStack: String,
)

/**
 * 학습 세트 한 개를 담는 임시 presentation 모델이다.
 *
 * @property id 세트 식별자
 * @property title 세트 제목 (예: "Set 1")
 * @property description 세트 설명
 * @property progress 세트 내 진행률(0..100)
 * @property totalSteps 전체 단계 수. 프로그레스 바 칸 수로도 쓰인다
 */
data class LearningSetItem(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int = 0,
    val totalSteps: Int = 7,
)

/**
 * 프로젝트 상세 화면에서 발생하는 일회성 부작용 이벤트다.
 */
sealed interface ProjectDetailEvent {
    /** 뒤로가기. */
    data object NavigateBack : ProjectDetailEvent

    /** 저장한 문제 화면으로 이동. */
    data object NavigateToSavedQuestions : ProjectDetailEvent

    /**
     * 문제 풀이 화면으로 이동.
     *
     * @property projectId 문제를 불러올 프로젝트 식별자
     */
    data class NavigateToQuiz(
        val projectId: String,
    ) : ProjectDetailEvent

    /** 학습 세트 진입.
     *
     * @property setId 진입할 세트 식별자
     */
    data class NavigateToLearningSet(
        val setId: String,
    ) : ProjectDetailEvent
}
