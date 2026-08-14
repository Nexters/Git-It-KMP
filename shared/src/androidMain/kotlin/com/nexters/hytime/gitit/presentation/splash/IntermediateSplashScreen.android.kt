package com.nexters.hytime.gitit.presentation.splash

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/** Android 시스템 바 아이콘을 흰색으로 표시하고 화면을 벗어나면 기존 상태로 복원한다. */
@Composable
internal actual fun IntermediateSplashSystemBarsEffect() {
    val view = LocalView.current
    val window = view.context.findActivity()?.window

    DisposableEffect(view, window) {
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val previousLightStatusBars = controller?.isAppearanceLightStatusBars
        val previousLightNavigationBars = controller?.isAppearanceLightNavigationBars

        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false

        onDispose {
            controller?.run {
                previousLightStatusBars?.let { isAppearanceLightStatusBars = it }
                previousLightNavigationBars?.let { isAppearanceLightNavigationBars = it }
            }
        }
    }
}

/**
 * Compose [Context]를 감싼 래퍼를 순회해 현재 [Activity]를 찾는다.
 *
 * @return 현재 화면의 Activity. Activity 컨텍스트가 아니면 `null`
 */
private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
