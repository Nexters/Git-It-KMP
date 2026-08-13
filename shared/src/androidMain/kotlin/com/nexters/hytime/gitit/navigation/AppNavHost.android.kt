package com.nexters.hytime.gitit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.nexters.hytime.gitit.feature.bookmark.BookmarkRoute
import com.nexters.hytime.gitit.feature.home.HomeRoute
import com.nexters.hytime.gitit.feature.my.MyRoute
import com.nexters.hytime.gitit.feature.my.SettingsScreen
import com.nexters.hytime.gitit.feature.onboarding.OnboardingRoute
import com.nexters.hytime.gitit.feature.projectdetail.ProjectDetailRoute
import com.nexters.hytime.gitit.feature.projectlist.ProjectListRoute
import com.nexters.hytime.gitit.feature.questioncreate.QuestionCreateRoute
import com.nexters.hytime.gitit.presentation.example.LiquidGlassExampleScreen
import com.nexters.hytime.gitit.presentation.signin.SignInScreen

@Composable
actual fun AppNavHost() {
    val uriHandler = LocalUriHandler.current
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
                    HomeRoute(
                        onNavigateToQuestionCreate = { backStack.add(AppRoute.QuestionCreate) },
                        onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                        onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                        onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
                    )
                }
                entry<AppRoute.Settings> {
                    SettingsScreen(
                        onBackClick = { backStack.removeLastOrNull() },
                        onPolicyClick = { uriHandler.openUri(POLICY_URL) },
                    )
                }
                entry<AppRoute.Bookmark> {
                    BookmarkRoute(
                        onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                        onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                        onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                    )
                }
                entry<AppRoute.My> {
                    MyRoute(
                        onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                        onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                        onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
                        onNavigateToSettings = { backStack.add(AppRoute.Settings) },
                    )
                }
                entry<AppRoute.ProjectList> {
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
                entry<AppRoute.ProjectDetail> { route ->
                    ProjectDetailRoute(
                        projectId = route.projectId,
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateToSavedQuestions = {},
                        onNavigateToLearningSet = {},
                    )
                }
                entry<AppRoute.QuestionCreate> {
                    QuestionCreateRoute(
                        onBackClick = { backStack.removeLastOrNull() },
                        onRepositoryConfirmed = {},
                    )
                }
                entry<AppRoute.LiquidGlassExample> {
                    LiquidGlassExampleScreen(onBackClick = { backStack.removeLastOrNull() })
                }
            },
    )
}
