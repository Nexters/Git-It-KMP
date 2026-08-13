package com.nexters.hytime.gitit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.rememberNavBackStack
import com.nexters.hytime.gitit.feature.bookmark.BookmarkRoute
import com.nexters.hytime.gitit.feature.home.HomeRoute
import com.nexters.hytime.gitit.feature.my.MyRoute
import com.nexters.hytime.gitit.feature.onboarding.OnboardingRoute
import com.nexters.hytime.gitit.feature.projectdetail.ProjectDetailRoute
import com.nexters.hytime.gitit.feature.projectlist.ProjectListRoute
import com.nexters.hytime.gitit.feature.questioncreate.QuestionCreateRoute
import com.nexters.hytime.gitit.presentation.example.LiquidGlassExampleScreen

// NavDisplay가 JVM(Desktop)을 미지원하므로 백스택 기반 직접 렌더를 사용한다.
@Composable
actual fun AppNavHost() {
    val backStack =
        rememberNavBackStack(
            appRouteSavedStateConfiguration,
            AppRoute.Onboarding,
        )

    fun navigateToMainRoute(route: AppRoute) {
        while (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
        if (route != AppRoute.Home && backStack.lastOrNull() != route) {
            backStack.add(route)
        }
    }

    when (val route = backStack.lastOrNull()) {
        AppRoute.Onboarding -> OnboardingRoute(onNavigateToHome = { backStack[0] = AppRoute.Home })
        AppRoute.Home -> {
            HomeRoute(
                onNavigateToQuestionCreate = { backStack.add(AppRoute.QuestionCreate) },
                onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
            )
        }
        AppRoute.Bookmark -> {
            BookmarkRoute(
                onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
            )
        }
        AppRoute.My -> {
            MyRoute(
                onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
            )
        }
        AppRoute.ProjectList -> {
            ProjectListRoute(
                onBackClick =
                    if (backStack.size > 1) {
                        { backStack.removeLastOrNull() }
                    } else {
                        null
                    },
                onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
            )
        }
        is AppRoute.ProjectDetail -> {
            ProjectDetailRoute(
                projectId = route.projectId,
                onBackClick = { backStack.removeLastOrNull() },
                onNavigateToSavedQuestions = {},
                onNavigateToLearningSet = {},
            )
        }
        AppRoute.QuestionCreate -> {
            QuestionCreateRoute(
                onBackClick = { backStack.removeLastOrNull() },
                onRepositoryConfirmed = {},
            )
        }
        AppRoute.LiquidGlassExample -> {
            LiquidGlassExampleScreen(onBackClick = { backStack.removeLastOrNull() })
        }
        else -> Unit
    }
}
