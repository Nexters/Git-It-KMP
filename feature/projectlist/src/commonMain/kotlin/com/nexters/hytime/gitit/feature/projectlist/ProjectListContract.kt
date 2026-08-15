package com.nexters.hytime.gitit.feature.projectlist

/**
 * 프로젝트 리스트 화면의 단일 UI 상태다.
 *
 * @property projects 사용자가 참여 중인 프로젝트 목록
 */
data class ProjectListUiState(
    val projects: List<ProjectListItem> = emptyList(),
)

/**
 * 프로젝트 카드 한 개를 표현하는 임시 presentation 모델이다.
 *
 * @property id 프로젝트 식별자
 * @property title 카드에 표시할 프로젝트 이름
 * @property techStack 프로젝트의 주요 기술 스택 요약
 * @property setLabel 최근 학습 세트 라벨
 * @property recentSetTitle 최근 학습 세트 이름
 * @property progress 최근 세트 진행률(0..100)
 */
data class ProjectListItem(
    val id: String,
    val title: String,
    val techStack: String,
    val setLabel: String,
    val recentSetTitle: String,
    val progress: Int,
)

/**
 * 프로젝트 리스트 화면에서 발생하는 사용자 의도다.
 */
sealed interface ProjectListIntent {
    /** 홈 탭 선택. */
    data object HomeTabClick : ProjectListIntent

    /** 프로젝트 탭 선택. 현재 화면이므로 이동하지 않는다. */
    data object ProjectTabClick : ProjectListIntent

    /** 저장 탭 선택. */
    data object SavedTabClick : ProjectListIntent

    /** 마이 탭 선택. */
    data object MyTabClick : ProjectListIntent

    /** 팝업 메뉴에서 프로젝트 삭제 선택. */
    data object DeleteMenuClick : ProjectListIntent

    /** 프로젝트 삭제 화면에서 뒤로가기 선택. */
    data object DeleteModeBackClick : ProjectListIntent

    /**
     * 삭제 화면에서 프로젝트의 마이너스 버튼 선택.
     *
     * @property projectId 삭제할 프로젝트 식별자
     */
    data class DeleteProjectClick(
        val projectId: String,
    ) : ProjectListIntent

    /**
     * 문제풀이 버튼 선택.
     *
     * @property projectId 문제풀이를 시작할 프로젝트 식별자
     */
    data class PlayProjectClick(
        val projectId: String,
    ) : ProjectListIntent
}

/**
 * 프로젝트 리스트 화면이 한 번만 전달해야 하는 이벤트다.
 */
sealed interface ProjectListSideEffect {
    /** 프로젝트 삭제 화면으로 이동. */
    data object NavigateToProjectDelete : ProjectListSideEffect

    /** 현재 화면을 닫고 이전 화면으로 이동. */
    data object NavigateBack : ProjectListSideEffect

    /** 홈 화면으로 이동. */
    data object NavigateToHome : ProjectListSideEffect

    /** 마이 화면으로 이동. */
    data object NavigateToMy : ProjectListSideEffect

    /** 저장한 문제 화면으로 이동. */
    data object NavigateToBookmark : ProjectListSideEffect

    /**
     * 문제 풀이 화면으로 이동.
     *
     * @property projectId 문제를 불러올 프로젝트 식별자
     */
    data class NavigateToQuiz(
        val projectId: String,
    ) : ProjectListSideEffect
}
