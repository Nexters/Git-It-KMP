package com.nexters.hytime.gitit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.nexters.hytime.gitit.feature.home.HomeRoute
import com.nexters.hytime.gitit.feature.onboarding.OnboardingRoute
import com.nexters.hytime.gitit.feature.projectdetail.ProjectDetailRoute
import com.nexters.hytime.gitit.feature.projectlist.ProjectListRoute
import com.nexters.hytime.gitit.presentation.example.LiquidGlassExampleScreen
import com.nexters.hytime.gitit.presentation.signin.SignInScreen

@Composable
actual fun AppNavHost() {
    val backStack =
        rememberNavBackStack(
            appRouteSavedStateConfiguration,
            AppRoute.Home,
        )

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<AppRoute.SignIn> {
                    SignInScreen()
                }
                entry<AppRoute.Onboarding> {
                    OnboardingRoute(onNavigateToHome = { backStack.add(AppRoute.Home) })
                }
                entry<AppRoute.Home> {
                    HomeRoute(onNavigateToProjectList = { backStack.add(AppRoute.ProjectList) })
                }
                entry<AppRoute.ProjectList> {
                    ProjectListRoute(
                        onBackClick =
                            if (backStack.size > 1) {
                                { backStack.removeLastOrNull() }
                            } else {
                                null
                            },
                        onNavigateToHome = { backStack.removeLastOrNull() },
                    )
                }
                entry<AppRoute.ProjectDetail> { route ->
                    ProjectDetailRoute(
                        projectId = route.projectId,
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateToSavedQuestions = {},
                        onNavigateToLearningSet = {},
                    )
                }
                entry<AppRoute.LiquidGlassExample> {
                    LiquidGlassExampleScreen(onBackClick = { backStack.removeLastOrNull() })
                }
            },
    )
}
