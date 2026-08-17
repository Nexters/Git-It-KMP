package com.nexters.hytime.gitit.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 앱에서 표시할 최상위 화면 목적지를 정의한다.
 */
@Serializable
sealed interface AppRoute : NavKey {
    /** 앱 시작 시 로그인 세션을 확인하는 스플래시 화면이다. */
    @Serializable
    data object Splash : AppRoute

    /**
     * 사용자가 저장한 문제 목록 화면이다.
     */
    @Serializable
    data object Bookmark : AppRoute

    /**
     * 앱을 시작할 때 표시하는 온보딩 화면이다.
     */
    @Serializable
    data object Onboarding : AppRoute

    /**
     * 튜토리얼 완료 후 홈으로 진입하기 전에 잠시 표시하는 중간 화면이다.
     */
    @Serializable
    data object IntermediateSplash : AppRoute

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

    /** 사용자 학습 환경과 계정을 관리하는 설정 화면이다. */
    @Serializable
    data object Settings : AppRoute

    /** 계정 삭제 전 주의사항을 안내하는 화면이다. */
    @Serializable
    data object AccountDelete : AppRoute

    /**
     * 사용자가 불러온 프로젝트 목록 화면이다.
     */
    @Serializable
    data object ProjectList : AppRoute

    /**
     * 사용자가 프로젝트를 삭제할 수 있는 목록 화면이다.
     */
    @Serializable
    data object ProjectDelete : AppRoute

    /**
     * 질문 생성을 시작할 GitHub 저장소를 입력하고 확인하는 화면이다.
     *
     * @property repositoryUrl 외부 공유로 미리 채울 저장소 URL. 일반 진입이면 빈 문자열
     */
    @Serializable
    data class ProjectLoad(
        val repositoryUrl: String = "",
    ) : AppRoute

    /**
     * 선택한 저장소의 문제 생성 조건을 설정하고 생성 진행 상태를 표시한다.
     *
     * @property repositoryUrl 프로젝트로 등록할 GitHub 저장소 URL
     */
    @Serializable
    data class QuizCreate(
        val repositoryUrl: String,
    ) : AppRoute

    /**
     * 사용자가 프로젝트 또는 학습 세트의 문제를 푸는 화면이다.
     *
     * @property projectId 문제를 불러올 프로젝트 식별자
     * @property setId 문제를 특정 학습 세트로 제한할 때 사용하는 식별자
     */
    @Serializable
    data class Quiz(
        val projectId: String,
        val setId: String? = null,
    ) : AppRoute

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
