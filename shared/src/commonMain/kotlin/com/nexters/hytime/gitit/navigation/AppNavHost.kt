package com.nexters.hytime.gitit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal val appRouteSavedStateConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(AppRoute.Bookmark.serializer())
                    subclass(AppRoute.Home.serializer())
                    subclass(AppRoute.My.serializer())
                    subclass(AppRoute.LiquidGlassExample.serializer())
                    subclass(AppRoute.SignIn.serializer())
                    subclass(AppRoute.Onboarding.serializer())
                    subclass(AppRoute.ProjectDetail.serializer())
                    subclass(AppRoute.ProjectList.serializer())
                }
            }
    }

/**
 * 플랫폼별 NavDisplay 렌더링을 분기한다.
 *
 * Android는 Nav3의 NavDisplay를 사용하고,
 * Desktop은 백스택 기반 직접 렌더를 사용한다 (NavDisplay가 JVM을 미지원).
 *
 * @param isSignedIn 저장된 로그인 세션이 있는지 여부
 */
@Composable
expect fun AppNavHost(isSignedIn: Boolean)
