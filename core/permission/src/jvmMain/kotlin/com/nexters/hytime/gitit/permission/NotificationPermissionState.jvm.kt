package com.nexters.hytime.gitit.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import javax.swing.SwingUtilities

/**
 * 현재 Desktop 운영체제에 맞는 알림 권한 상태를 기억한다.
 *
 * macOS에서는 Apple UserNotifications 프레임워크의 실제 권한을 조회하고 요청한다. 그 외 Desktop
 * 운영체제에서는 별도의 런타임 권한 요청이 필요하지 않은 상태를 반환한다.
 *
 * @return 현재 운영체제의 알림 권한 객체
 */
@Composable
actual fun rememberNotificationPermissionState(): NotificationPermissionState {
    if (!isMacOs()) return DesktopNotificationPermissionState

    val statusState = remember { mutableStateOf(NotificationPermissionStatus.NOT_DETERMINED) }
    val permissionState =
        remember(statusState) {
            runCatching {
                MacOsNotificationPermissionState(
                    statusState = statusState,
                    bridge = MacOsNotificationPermissionBridge(),
                )
            }.getOrElse { UnavailableNotificationPermissionState }
        }

    if (permissionState is MacOsNotificationPermissionState) {
        SideEffect {
            permissionState.refresh()
        }
    }

    return permissionState
}

/**
 * macOS 네이티브 권한 상태와 요청 동작을 Compose 상태로 노출한다.
 *
 * @property statusState 권한 결과를 보관하는 Compose 상태
 * @property bridge Apple UserNotifications 프레임워크를 호출하는 네이티브 브리지
 */
private class MacOsNotificationPermissionState(
    private val statusState: MutableState<NotificationPermissionStatus>,
    private val bridge: MacOsNotificationPermissionBridge,
) : NotificationPermissionState {
    private val callback =
        MacOsNotificationPermissionCallback { nativeStatus ->
            val status = nativeStatus.toNotificationPermissionStatus()
            SwingUtilities.invokeLater {
                statusState.value = status
            }
        }

    override val status: NotificationPermissionStatus
        get() = statusState.value

    override fun requestPermission() {
        if (status == NotificationPermissionStatus.NOT_DETERMINED) {
            bridge.requestPermission(callback)
        }
    }

    /**
     * 시스템 설정에 저장된 최신 알림 권한 상태를 다시 조회한다.
     */
    fun refresh() {
        bridge.getPermissionStatus(callback)
    }
}

/**
 * 런타임 권한 요청이 없는 Desktop 환경의 알림 권한 상태다.
 */
private object DesktopNotificationPermissionState : NotificationPermissionState {
    override val status: NotificationPermissionStatus = NotificationPermissionStatus.NOT_REQUIRED

    override fun requestPermission() = Unit
}

/**
 * macOS 네이티브 브리지를 초기화할 수 없을 때 노출하는 권한 상태다.
 */
private object UnavailableNotificationPermissionState : NotificationPermissionState {
    override val status: NotificationPermissionStatus = NotificationPermissionStatus.UNAVAILABLE

    override fun requestPermission() = Unit
}

/**
 * 현재 JVM이 macOS에서 실행 중인지 확인한다.
 *
 * @return 운영체제 이름이 macOS 계열이면 `true`
 */
private fun isMacOs(): Boolean = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
