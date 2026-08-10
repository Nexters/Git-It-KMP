package com.nexters.hytime.gitit.permission

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * macOS 네이티브 권한 상태 코드의 공통 상태 변환을 검증한다.
 */
class MacOsNotificationPermissionBridgeTest {
    /**
     * macOS에서 애플리케이션 리소스에 포함된 네이티브 라이브러리를 JNA로 로드할 수 있어야 한다.
     */
    @Test
    fun macOsNotificationPermissionBridge_nativeLibraryExists_loadsSuccessfully() {
        if (!System.getProperty("os.name").startsWith("Mac", ignoreCase = true)) return

        MacOsNotificationPermissionBridge()
    }

    /**
     * 사용자가 아직 선택하지 않은 상태를 권한 요청 가능한 상태로 유지해야 한다.
     */
    @Test
    fun toNotificationPermissionStatus_notDetermined_returnsNotDetermined() {
        assertEquals(
            expected = NotificationPermissionStatus.NOT_DETERMINED,
            actual = 0.toNotificationPermissionStatus(),
        )
    }

    /**
     * 거부 상태를 공통 거부 상태로 변환해야 한다.
     */
    @Test
    fun toNotificationPermissionStatus_denied_returnsDenied() {
        assertEquals(
            expected = NotificationPermissionStatus.DENIED,
            actual = 1.toNotificationPermissionStatus(),
        )
    }

    /**
     * 일반 허용과 provisional 허용은 모두 알림을 게시할 수 있는 상태여야 한다.
     */
    @Test
    fun toNotificationPermissionStatus_authorizedStatuses_returnsGranted() {
        assertEquals(
            expected = NotificationPermissionStatus.GRANTED,
            actual = 2.toNotificationPermissionStatus(),
        )
        assertEquals(
            expected = NotificationPermissionStatus.GRANTED,
            actual = 3.toNotificationPermissionStatus(),
        )
    }

    /**
     * 알 수 없는 네이티브 코드는 지원 불가 상태로 처리해야 한다.
     */
    @Test
    fun toNotificationPermissionStatus_unknown_returnsUnavailable() {
        assertEquals(
            expected = NotificationPermissionStatus.UNAVAILABLE,
            actual = (-1).toNotificationPermissionStatus(),
        )
    }
}
