package com.nexters.hytime.gitit.permission

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 알림 런타임 권한 조건을 공통 상태로 변환하는 정책을 검증한다.
 */
class NotificationPermissionStatusTest {
    /**
     * 런타임 권한을 지원하지 않는 환경은 시스템 권한 값과 무관하게 요청 불필요 상태여야 한다.
     */
    @Test
    fun resolveNotificationPermissionStatus_runtimePermissionIsNotRequired_returnsNotRequired() {
        assertEquals(
            expected = NotificationPermissionStatus.NOT_REQUIRED,
            actual =
                resolveNotificationPermissionStatus(
                    isRuntimePermissionRequired = false,
                    isGranted = false,
                ),
        )
    }

    /**
     * 런타임 권한이 필요한 환경에서 시스템이 허용한 결과를 그대로 반영해야 한다.
     */
    @Test
    fun resolveNotificationPermissionStatus_runtimePermissionIsGranted_returnsGranted() {
        assertEquals(
            expected = NotificationPermissionStatus.GRANTED,
            actual =
                resolveNotificationPermissionStatus(
                    isRuntimePermissionRequired = true,
                    isGranted = true,
                ),
        )
    }

    /**
     * 런타임 권한이 필요한 환경에서 거부된 결과를 요청 가능한 상태로 유지해야 한다.
     */
    @Test
    fun resolveNotificationPermissionStatus_runtimePermissionIsDenied_returnsDenied() {
        assertEquals(
            expected = NotificationPermissionStatus.DENIED,
            actual =
                resolveNotificationPermissionStatus(
                    isRuntimePermissionRequired = true,
                    isGranted = false,
                ),
        )
    }
}
