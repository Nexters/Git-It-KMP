package com.nexters.hytime.gitit.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android의 알림 런타임 권한 상태를 기억한다.
 *
 * Android 13(API 33) 이상에서는 `POST_NOTIFICATIONS` 권한을 요청하고, 이전 버전에서는
 * 런타임 권한이 필요하지 않은 상태를 반환한다.
 *
 * @return Android 알림 권한 상태와 요청 동작을 제공하는 객체
 */
@Composable
actual fun rememberNotificationPermissionState(): NotificationPermissionState {
    val context = LocalContext.current
    val status =
        remember(context) {
            mutableStateOf(context.currentNotificationPermissionStatus())
        }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            status.value =
                resolveNotificationPermissionStatus(
                    isRuntimePermissionRequired = true,
                    isGranted = isGranted,
                )
        }

    SideEffect {
        status.value = context.currentNotificationPermissionStatus()
    }

    return remember(context, launcher, status) {
        AndroidNotificationPermissionState(status) {
            if (status.value == NotificationPermissionStatus.DENIED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

/**
 * Compose 상태와 Android 권한 요청 런처를 [NotificationPermissionState]로 감싼다.
 *
 * @property statusState 권한 결과를 보관하는 Compose 상태
 * @property requestPermissionAction Android 권한 요청을 실행하는 동작
 */
private class AndroidNotificationPermissionState(
    private val statusState: MutableState<NotificationPermissionStatus>,
    private val requestPermissionAction: () -> Unit,
) : NotificationPermissionState {
    override val status: NotificationPermissionStatus
        get() = statusState.value

    override fun requestPermission() {
        requestPermissionAction()
    }
}

/**
 * Android OS 버전과 패키지 권한을 현재 알림 권한 상태로 변환한다.
 *
 * @return Android 13 이상에서는 실제 권한 결과, 이전 버전에서는 요청 불필요 상태
 */
private fun Context.currentNotificationPermissionStatus(): NotificationPermissionStatus {
    val isRuntimePermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val isGranted =
        !isRuntimePermissionRequired ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    return resolveNotificationPermissionStatus(
        isRuntimePermissionRequired = isRuntimePermissionRequired,
        isGranted = isGranted,
    )
}
