package com.nexters.hytime.gitit.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 앱에서 표시할 최상위 화면 목적지를 정의한다.
 */
@Serializable
sealed interface AppRoute : NavKey {
    /**
     * 앱을 시작할 때 표시하는 로그인 화면이다.
     */
    @Serializable
    data object SignIn : AppRoute

    /**
     * 앱을 시작할 때 표시하는 홈 화면이다.
     */
    @Serializable
    data object Home : AppRoute

    /**
     * 리퀴드 글래스 백버튼 확인용 예제 화면이다.
     */
    @Serializable
    data object LiquidGlassBackButtonExample : AppRoute
}
