package com.nexters.hytime.gitit.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 앱에서 표시할 최상위 화면 목적지를 정의한다.
 */
@Serializable
sealed interface AppRoute : NavKey {
    /**
     * 사용자가 저장한 문제 목록 화면이다.
     */
    @Serializable
    data object Bookmark : AppRoute

    /**
     * Google 로그인을 수행하는 로그인 화면이다.
     */
    @Serializable
    data object SignIn : AppRoute

    /**
     * 앱을 시작할 때 표시하는 온보딩 화면이다.
     */
    @Serializable
    data object Onboarding : AppRoute

    /**
     * 앱을 시작할 때 표시하는 홈 화면이다.
     */
    @Serializable
    data object Home : AppRoute

    /**
     * 사용자 학습 현황을 표시하는 마이 화면이다.
     */
    @Serializable
    data object My : AppRoute

    /**
     * 사용자가 불러온 프로젝트 목록 화면이다.
     */
    @Serializable
    data object ProjectList : AppRoute

    /**
     * 질문 생성을 시작할 GitHub 저장소를 입력하고 확인하는 화면이다.
     */
    @Serializable
    data object QuestionCreate : AppRoute

    /**
     * 리퀴드 글래스 디자인 컴포넌트 확인용 예제 화면이다.
     */
    @Serializable
    data object LiquidGlassExample : AppRoute

    /**
     * 프로젝트 상세 화면이다. [projectId]로 특정 프로젝트를 식별한다.
     *
     * @property projectId 대상 프로젝트 식별자
     */
    @Serializable
    data class ProjectDetail(
        val projectId: String,
    ) : AppRoute
}
