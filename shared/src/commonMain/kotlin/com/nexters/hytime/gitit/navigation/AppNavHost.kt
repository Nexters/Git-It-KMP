package com.nexters.hytime.gitit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/** 설정 화면에서 연결할 서비스 약관 및 정책 문서 주소다. */
internal const val POLICY_URL =
    "https://app.notion.com/p/Git-it-3bb7221e5fe78005bcd9fab953906df1?source=copy_link"

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
                    subclass(AppRoute.LiquidGlassExample.serializer())
                    subclass(AppRoute.Onboarding.serializer())
                    subclass(AppRoute.IntermediateSplash.serializer())
                    subclass(AppRoute.ProjectDetail.serializer())
                    subclass(AppRoute.ProjectList.serializer())
                    subclass(AppRoute.ProjectLoad.serializer())
                    subclass(AppRoute.QuizCreate.serializer())
                    subclass(AppRoute.Quiz.serializer())
                }
            }
    }

/**
 * 플랫폼별 NavDisplay 렌더링을 분기한다.
 *
 * Android는 Nav3의 NavDisplay를 사용하고,
 * Desktop은 백스택 기반 직접 렌더를 사용한다 (NavDisplay가 JVM을 미지원).
 */
@Composable
expect fun AppNavHost()
