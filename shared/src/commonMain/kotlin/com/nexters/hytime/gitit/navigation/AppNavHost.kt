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
