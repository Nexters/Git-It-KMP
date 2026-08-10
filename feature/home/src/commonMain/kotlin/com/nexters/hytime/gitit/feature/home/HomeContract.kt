package com.nexters.hytime.gitit.feature.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * 홈에서 이어서 학습할 레포지토리 카드 정보다.
 *
 * @property id 클릭 이벤트에서 레포지토리를 식별하는 값
 * @property title 카드 상단에 표시할 이름
 * @property technologies 레포지토리에서 사용하는 주요 기술
 * @property setLabel 현재 학습 세트 문구
 * @property description 현재 학습 내용 요약
 * @property progress 0f..1f 범위의 학습 진행률
 * @property backgroundColor 카드 배경색
 */
@Immutable
data class HomeLearningRepository(
    val id: String,
    val title: String,
    val technologies: String,
    val setLabel: String,
    val description: String,
    val progress: Float,
    val backgroundColor: Color,
)

/**
 * 홈 화면이 표시할 상태를 정의한다.
 *
 * @property userName 상단 프로필에 표시할 사용자 이름
 * @property userRole 사용자 이름 아래에 표시할 역할
 * @property learningRepositories 이어서 학습할 레포지토리 목록. 비어 있으면 빈 상태를 표시한다
 */
data class HomeUiState(
    val userName: String = "김이박",
    val userRole: String = "Junior Developer",
    val learningRepositories: List<HomeLearningRepository> = emptyList(),
)

/** 사용자가 홈 화면에서 발생시킨 의도를 정의한다. */
sealed interface HomeIntent {
    /** 홈 콘텐츠를 새로고침하도록 요청한다. */
    data object Refresh : HomeIntent

    /** 홈 탭을 선택한다. */
    data object HomeTabClick : HomeIntent

    /** 프로젝트 불러오기 버튼을 선택한다. */
    data object LoadProjectClick : HomeIntent

    /** 학습 중인 레포지토리 전체 보기를 선택한다. */
    data object ViewAllProjectsClick : HomeIntent

    /**
     * 학습 카드를 선택한다.
     *
     * @property repositoryId 선택한 레포지토리 식별자
     */
    data class LearningCardClick(
        val repositoryId: String,
    ) : HomeIntent

    /**
     * 학습 카드의 재생 버튼을 선택한다.
     *
     * @property repositoryId 학습을 시작할 레포지토리 식별자
     */
    data class LearningPlayClick(
        val repositoryId: String,
    ) : HomeIntent

    /** 프로젝트 탭을 선택한다. */
    data object ProjectTabClick : HomeIntent

    /** 저장 탭을 선택한다. */
    data object SavedTabClick : HomeIntent

    /** 마이 탭을 선택한다. */
    data object MyTabClick : HomeIntent
}

/** 홈 화면이 한 번만 전달해야 하는 이벤트를 정의한다. */
sealed interface HomeSideEffect {
    /** 프로젝트 리스트 화면으로 이동한다. */
    data object NavigateToProjectList : HomeSideEffect

    /** 마이 화면으로 이동한다. */
    data object NavigateToMy : HomeSideEffect

    /** 저장한 문제 화면으로 이동한다. */
    data object NavigateToBookmark : HomeSideEffect
}
