package com.nexters.hytime.gitit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import androidx.savedstate.serialization.SavedStateConfiguration
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.feature.bookmark.BookmarkRoute
import com.nexters.hytime.gitit.feature.home.HomeRoute
import com.nexters.hytime.gitit.feature.my.AccountDeleteRoute
import com.nexters.hytime.gitit.feature.my.MyRoute
import com.nexters.hytime.gitit.feature.my.SettingsScreen
import com.nexters.hytime.gitit.feature.onboarding.OnboardingRoute
import com.nexters.hytime.gitit.feature.projectdetail.ProjectDetailRoute
import com.nexters.hytime.gitit.feature.projectlist.ProjectListRoute
import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateRoute
import com.nexters.hytime.gitit.feature.quiz.load.ProjectLoadRoute
import com.nexters.hytime.gitit.feature.quiz.solve.SolveQuizRoute
import com.nexters.hytime.gitit.permission.rememberNotificationPermissionState
import com.nexters.hytime.gitit.presentation.example.LiquidGlassExampleScreen
import com.nexters.hytime.gitit.presentation.splash.IntermediateSplashScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/** 설정 화면에서 연결할 서비스 약관 및 정책 문서 주소다. */
internal const val POLICY_URL =
    "https://app.notion.com/p/Git-it-3bb7221e5fe78005bcd9fab953906df1?source=copy_link"

/** 메인 화면 전환에 사용하는 페이드 시간이다. */
private const val FADE_DURATION_MILLIS = 180

/** 계층형 화면과 집중형 화면 전환에 사용하는 이동 시간이다. */
private const val SLIDE_DURATION_MILLIS = 300

/**
 * 백스택을 저장 상태로 직렬화할 때 쓰는 설정이다.
 *
 * Nav3는 백스택 원소를 [NavKey] 타입으로 직렬화하는데, [NavKey]는 sealed가 아니라서
 * [AppRoute]가 `@Serializable sealed interface`여도 서브타입이 자동 등록되지 않는다.
 * **경로를 추가하면 여기에도 반드시 등록해야 한다.** 누락하면 화면이 백스택에 올라간 상태로
 * 앱이 백그라운드로 갈 때 `SerializationException`으로 크래시한다.
 */
internal val appRouteSavedStateConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(AppRoute.Bookmark.serializer())
                    subclass(AppRoute.Home.serializer())
                    subclass(AppRoute.My.serializer())
                    subclass(AppRoute.Settings.serializer())
                    subclass(AppRoute.AccountDelete.serializer())
                    subclass(AppRoute.LiquidGlassExample.serializer())
                    subclass(AppRoute.Onboarding.serializer())
                    subclass(AppRoute.IntermediateSplash.serializer())
                    subclass(AppRoute.ProjectDetail.serializer())
                    subclass(AppRoute.ProjectDelete.serializer())
                    subclass(AppRoute.ProjectList.serializer())
                    subclass(AppRoute.ProjectLoad.serializer())
                    subclass(AppRoute.QuizCreate.serializer())
                    subclass(AppRoute.Quiz.serializer())
                }
            }
    }

/** 화면의 역할에 따라 적용할 전환 유형이다. */
internal enum class AppNavigationMotion {
    Fade,
    Horizontal,
    Vertical,
}

/**
 * 화면의 역할에 맞는 전환 유형을 반환한다.
 *
 * @return 메인·모드 화면은 페이드, 계층형 화면은 가로 이동, 집중형 화면은 세로 이동
 */
internal fun AppRoute.navigationMotion(): AppNavigationMotion =
    when (this) {
        AppRoute.Bookmark,
        AppRoute.Home,
        AppRoute.IntermediateSplash,
        AppRoute.My,
        AppRoute.Onboarding,
        AppRoute.ProjectDelete,
        AppRoute.ProjectList,
        -> AppNavigationMotion.Fade

        AppRoute.AccountDelete,
        AppRoute.LiquidGlassExample,
        is AppRoute.ProjectDetail,
        AppRoute.Settings,
        -> AppNavigationMotion.Horizontal

        AppRoute.ProjectLoad,
        is AppRoute.QuizCreate,
        is AppRoute.Quiz,
        -> AppNavigationMotion.Vertical
    }

/**
 * 화면 역할과 이동 방향에 맞는 애니메이션을 만든다.
 *
 * @param motion 적용할 화면 전환 유형
 * @param isPop 뒤로가기로 이전 화면을 표시하는 전환인지 여부
 * @return Navigation 3가 두 화면 사이에 적용할 전환
 */
private fun AnimatedContentTransitionScope<Scene<*>>.appNavigationTransform(
    motion: AppNavigationMotion,
    isPop: Boolean,
): ContentTransform =
    when (motion) {
        AppNavigationMotion.Fade ->
            if (isPop) {
                EnterTransition.None togetherWith fadeOut(animationSpec = tween(FADE_DURATION_MILLIS))
            } else {
                fadeIn(animationSpec = tween(FADE_DURATION_MILLIS)) togetherWith ExitTransition.None
            }

        AppNavigationMotion.Horizontal -> {
            val direction =
                if (isPop) {
                    AnimatedContentTransitionScope.SlideDirection.Right
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Left
                }

            slideIntoContainer(direction, animationSpec = tween(SLIDE_DURATION_MILLIS)) togetherWith
                slideOutOfContainer(direction, animationSpec = tween(SLIDE_DURATION_MILLIS))
        }

        AppNavigationMotion.Vertical ->
            if (isPop) {
                EnterTransition.None togetherWith
                    slideOutVertically(animationSpec = tween(SLIDE_DURATION_MILLIS)) { it }
            } else {
                slideInVertically(animationSpec = tween(SLIDE_DURATION_MILLIS)) { it } togetherWith
                    ExitTransition.None
            }
    }

/**
 * 예측 뒤로가기 제스처의 시작 가장자리를 따라 화면을 이동시킨다.
 *
 * @param swipeEdge 사용자가 뒤로가기 제스처를 시작한 화면 가장자리
 * @return 축소 효과 없이 이전 화면으로 이동하는 전환
 */
private fun AnimatedContentTransitionScope<Scene<*>>.appPredictivePopTransform(
    @NavigationEvent.SwipeEdge swipeEdge: Int,
): ContentTransform {
    val direction =
        if (swipeEdge == NavigationEvent.EDGE_RIGHT) {
            AnimatedContentTransitionScope.SlideDirection.Left
        } else {
            AnimatedContentTransitionScope.SlideDirection.Right
        }

    return slideIntoContainer(direction, animationSpec = tween(SLIDE_DURATION_MILLIS)) togetherWith
        slideOutOfContainer(direction, animationSpec = tween(SLIDE_DURATION_MILLIS))
}

/**
 * 화면 경로에 맞는 Navigation 3 전환 메타데이터를 만든다.
 *
 * @return 앞으로 이동과 뒤로가기 전환이 등록된 메타데이터
 */
private fun AppRoute.navigationMetadata(): Map<String, Any> {
    val motion = navigationMotion()

    return metadata {
        put(NavDisplay.TransitionKey) { appNavigationTransform(motion, isPop = false) }
        put(NavDisplay.PopTransitionKey) { appNavigationTransform(motion, isPop = true) }
        put(NavDisplay.PredictivePopTransitionKey) { swipeEdge -> appPredictivePopTransform(swipeEdge) }
    }
}

/**
 * 화면마다 독립된 상태 저장소와 ViewModel 저장소를 부여하는 데코레이터 목록을 만든다.
 *
 * ViewModel 데코레이터가 없으면 `LocalViewModelStoreOwner`가 Activity나 윈도우로 폴백해
 * ViewModel이 앱 수명 내내 살아남고, 화면을 다시 열어도 이전 방문의 상태가 남는다.
 *
 * @return NavDisplay 기본 상태 저장 데코레이터에 ViewModel 화면 스코프를 더한 목록
 */
@Composable
private fun rememberAppNavEntryDecorators(): List<NavEntryDecorator<NavKey>> =
    listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    )

/** 앱의 모든 화면 경로를 백스택에 따라 표시한다. */
@Composable
fun AppNavHost() {
    val uriHandler = LocalUriHandler.current
    val notificationPermissionState = rememberNotificationPermissionState()
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

    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize().background(GitItTheme.colors.grey700),
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = rememberAppNavEntryDecorators(),
        entryProvider =
            entryProvider {
                entry<AppRoute.Onboarding>(metadata = AppRoute.Onboarding.navigationMetadata()) {
                    OnboardingRoute(
                        onNavigateToHome = { backStack[0] = AppRoute.Home },
                        onNavigateToIntermediateSplash = { backStack[0] = AppRoute.IntermediateSplash },
                    )
                }
                entry<AppRoute.IntermediateSplash>(metadata = AppRoute.IntermediateSplash.navigationMetadata()) {
                    IntermediateSplashScreen(onFinished = { backStack[0] = AppRoute.Home })
                }
                entry<AppRoute.Home>(metadata = AppRoute.Home.navigationMetadata()) {
                    QuizCreateHomeHost(
                        onNavigateToProject = { projectId -> backStack.add(AppRoute.ProjectDetail(projectId)) },
                    ) { isQuizCreating ->
                        HomeRoute(
                            isQuizCreating = isQuizCreating,
                            onNavigateToProjectLoad = { backStack.add(AppRoute.ProjectLoad) },
                            onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                            onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                            onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
                            onNavigateToProjectDetail = { projectId -> backStack.add(AppRoute.ProjectDetail(projectId)) },
                            onNavigateToQuiz = { projectId -> backStack.add(AppRoute.Quiz(projectId)) },
                        )
                    }
                }
                entry<AppRoute.Settings>(metadata = AppRoute.Settings.navigationMetadata()) {
                    SettingsScreen(
                        onBackClick = { backStack.removeLastOrNull() },
                        onPolicyClick = { uriHandler.openUri(POLICY_URL) },
                        onDeleteAccountClick = { backStack.add(AppRoute.AccountDelete) },
                    )
                }
                entry<AppRoute.AccountDelete>(metadata = AppRoute.AccountDelete.navigationMetadata()) {
                    AccountDeleteRoute(
                        onBackClick = { backStack.removeLastOrNull() },
                        onDeleteAccountClick = {},
                    )
                }
                entry<AppRoute.Bookmark>(metadata = AppRoute.Bookmark.navigationMetadata()) {
                    BookmarkRoute(
                        onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                        onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                        onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                    )
                }
                entry<AppRoute.My>(metadata = AppRoute.My.navigationMetadata()) {
                    MyRoute(
                        onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                        onNavigateToProjectList = { navigateToMainRoute(AppRoute.ProjectList) },
                        onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
                        onNavigateToSettings = { backStack.add(AppRoute.Settings) },
                    )
                }
                entry<AppRoute.ProjectList>(metadata = AppRoute.ProjectList.navigationMetadata()) {
                    ProjectListRoute(
                        onNavigateToProjectDelete = { backStack.add(AppRoute.ProjectDelete) },
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                        onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                        onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
                        onNavigateToProjectDetail = { projectId -> backStack.add(AppRoute.ProjectDetail(projectId)) },
                        onNavigateToQuiz = { projectId -> backStack.add(AppRoute.Quiz(projectId)) },
                    )
                }
                entry<AppRoute.ProjectDelete>(metadata = AppRoute.ProjectDelete.navigationMetadata()) {
                    ProjectListRoute(
                        isDeleteMode = true,
                        onNavigateToProjectDelete = {},
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                        onNavigateToMy = { navigateToMainRoute(AppRoute.My) },
                        onNavigateToBookmark = { navigateToMainRoute(AppRoute.Bookmark) },
                        onNavigateToProjectDetail = {},
                        onNavigateToQuiz = { projectId -> backStack.add(AppRoute.Quiz(projectId)) },
                    )
                }
                entry<AppRoute.ProjectDetail>(metadata = { it.navigationMetadata() }) { route ->
                    ProjectDetailRoute(
                        projectId = route.projectId,
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateToHome = { navigateToMainRoute(AppRoute.Home) },
                        onNavigateToSavedQuestions = {},
                        onNavigateToLearningSet = { projectId, setId -> backStack.add(AppRoute.Quiz(projectId, setId)) },
                        onNavigateToQuiz = { projectId -> backStack.add(AppRoute.Quiz(projectId)) },
                    )
                }
                entry<AppRoute.ProjectLoad>(metadata = AppRoute.ProjectLoad.navigationMetadata()) {
                    ProjectLoadRoute(
                        onBackClick = { backStack.removeLastOrNull() },
                        onRepositoryConfirmed = { repository ->
                            backStack.add(AppRoute.QuizCreate("https://github.com/${repository.ownerName}/${repository.name}"))
                        },
                    )
                }
                entry<AppRoute.QuizCreate>(metadata = { it.navigationMetadata() }) { route ->
                    QuizCreateRoute(
                        repositoryUrl = route.repositoryUrl,
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateHome = { navigateToMainRoute(AppRoute.Home) },
                        onRequestNotificationPermission = notificationPermissionState::requestPermission,
                    )
                }
                entry<AppRoute.Quiz>(metadata = { it.navigationMetadata() }) { route ->
                    SolveQuizRoute(
                        projectId = route.projectId,
                        setId = route.setId,
                        onBackClick = { backStack.removeLastOrNull() },
                    )
                }
                entry<AppRoute.LiquidGlassExample>(metadata = AppRoute.LiquidGlassExample.navigationMetadata()) {
                    LiquidGlassExampleScreen(onBackClick = { backStack.removeLastOrNull() })
                }
            },
    )
}
