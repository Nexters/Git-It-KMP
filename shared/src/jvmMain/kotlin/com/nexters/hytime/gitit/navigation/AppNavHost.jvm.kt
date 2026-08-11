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
import com.nexters.hytime.gitit.feature.quiz.QuizRoute
import com.nexters.hytime.gitit.presentation.example.LiquidGlassExampleScreen
import com.nexters.hytime.gitit.presentation.signin.SignInScreen

// NavDisplay가 JVM(Desktop)을 미지원하므로 백스택 기반 직접 렌더를 사용한다.
@Composable
actual fun AppNavHost() {
    val backStack =
        rememberNavBackStack(
            appRouteSavedStateConfiguration,
            AppRoute.Home,
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
        AppRoute.SignIn -> SignInScreen()
        AppRoute.Onboarding -> OnboardingRoute(onNavigateToHome = { backStack.add(AppRoute.Home) })
        AppRoute.Home -> {
            HomeRoute(
                onNavigateToQuestionCreate = { backStack.add(AppRoute.QuestionCreate) },
                onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
                onNavigateToQuiz = { backStack.add(AppRoute.Quiz) },
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
                onNavigateToQuiz = { backStack.add(AppRoute.Quiz) },
            )
        }
        is AppRoute.ProjectDetail -> {
            ProjectDetailRoute(
                projectId = route.projectId,
                onBackClick = { backStack.removeLastOrNull() },
                onNavigateToSavedQuestions = {},
                onNavigateToLearningSet = { backStack.add(AppRoute.Quiz) },
                onNavigateToQuiz = { backStack.add(AppRoute.Quiz) },
            )
        }
        AppRoute.QuestionCreate -> {
            QuestionCreateRoute(
                onBackClick = { backStack.removeLastOrNull() },
                onRepositoryConfirmed = {},
            )
        }
        AppRoute.Quiz -> {
            QuizRoute(onBackClick = { backStack.removeLastOrNull() })
        }
        AppRoute.LiquidGlassExample -> {
            LiquidGlassExampleScreen(onBackClick = { backStack.removeLastOrNull() })
        }
        else -> SignInScreen()
    }
}
