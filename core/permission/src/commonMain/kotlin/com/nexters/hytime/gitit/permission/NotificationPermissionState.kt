package com.nexters.hytime.gitit.permission

import androidx.compose.runtime.Composable

/**
 * 알림 권한의 현재 상태다.
 */
enum class NotificationPermissionStatus {
    /** 알림 권한이 허용되어 있다. */
    GRANTED,

    /** 알림 권한이 거부되어 재요청이나 시스템 설정 안내가 필요하다. */
    DENIED,

    /** 아직 사용자가 알림 권한 허용 여부를 선택하지 않았다. */
    NOT_DETERMINED,

    /** 현재 플랫폼이나 OS 버전에서는 알림 런타임 권한 요청이 필요하지 않다. */
    NOT_REQUIRED,

    /** 현재 실행 환경에서 알림 권한 상태를 확인할 수 없다. */
    UNAVAILABLE,
}

/**
 * 플랫폼의 알림 권한 상태를 관찰하고 권한을 요청한다.
 */
interface NotificationPermissionState {
    /**
     * 현재 알림 권한 상태다.
     */
    val status: NotificationPermissionStatus

    /**
     * 플랫폼에서 알림 런타임 권한이 필요하면 사용자에게 권한을 요청한다.
     *
     * 권한이 이미 허용되어 있거나 요청이 필요하지 않은 플랫폼에서는 아무 작업도 하지 않는다.
     * macOS에서 사용자가 이미 거부한 경우 시스템이 다시 대화상자를 표시하지 않는다.
     */
    fun requestPermission()
}

/**
 * 현재 플랫폼의 알림 권한 상태를 기억한다.
 *
 * @return 권한 결과를 Compose 상태로 노출하는 [NotificationPermissionState]
 */
@Composable
expect fun rememberNotificationPermissionState(): NotificationPermissionState

/**
 * 런타임 권한 지원 여부와 시스템 권한 결과를 공통 알림 권한 상태로 변환한다.
 *
 * @param isRuntimePermissionRequired 현재 플랫폼에서 런타임 권한 요청이 필요한지 여부
 * @param isGranted 시스템이 보고한 권한 허용 여부
 * @return 호출자가 처리할 공통 알림 권한 상태
 */
internal fun resolveNotificationPermissionStatus(
    isRuntimePermissionRequired: Boolean,
    isGranted: Boolean,
): NotificationPermissionStatus =
    when {
        !isRuntimePermissionRequired -> NotificationPermissionStatus.NOT_REQUIRED
        isGranted -> NotificationPermissionStatus.GRANTED
        else -> NotificationPermissionStatus.DENIED
    }
