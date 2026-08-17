package com.nexters.hytime.gitit.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

/** 화면별 네비게이션 전환 분류를 검증한다. */
class AppNavigationMotionTest {
    /** 모든 화면 경로를 역할에 맞는 전환 유형으로 분류한다. */
    @Test
    fun navigationMotion_routeCategory_returnsExpectedMotion() {
        val expected =
            mapOf<AppRoute, AppNavigationMotion>(
                AppRoute.Bookmark to AppNavigationMotion.Fade,
                AppRoute.Home to AppNavigationMotion.Fade,
                AppRoute.IntermediateSplash to AppNavigationMotion.Fade,
                AppRoute.My to AppNavigationMotion.Fade,
                AppRoute.Onboarding to AppNavigationMotion.Fade,
                AppRoute.ProjectDelete to AppNavigationMotion.Fade,
                AppRoute.ProjectList to AppNavigationMotion.Fade,
                AppRoute.Splash to AppNavigationMotion.Fade,
                AppRoute.AccountDelete to AppNavigationMotion.Horizontal,
                AppRoute.LiquidGlassExample to AppNavigationMotion.Horizontal,
                AppRoute.ProjectDetail(projectId = "project-1") to AppNavigationMotion.Horizontal,
                AppRoute.Settings to AppNavigationMotion.Horizontal,
                AppRoute.ProjectLoad to AppNavigationMotion.Vertical,
                AppRoute.QuizCreate(repositoryUrl = "https://github.com/Nexters/Git-It-KMP") to AppNavigationMotion.Vertical,
                AppRoute.Quiz(projectId = "project-1") to AppNavigationMotion.Vertical,
            )

        assertEquals(expected, expected.keys.associateWith { it.navigationMotion() })
    }
}
