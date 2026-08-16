package com.nexters.hytime.gitit.notification

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** FCM data payload가 시스템 알림 내용으로 변환되는 조건을 검증한다. */
class NotificationContentTest {
    /** 제목과 본문이 있으면 주변 공백을 제거한 알림 내용을 반환하는지 검증한다. */
    @Test
    fun toNotificationContent_제목과본문이있으면_알림내용을반환한다() {
        val content = mapOf("title" to " 새 학습 세트 ", "body" to " 문제가 준비됐어요. ").toNotificationContent()

        assertEquals(NotificationContent("새 학습 세트", "문제가 준비됐어요."), content)
    }

    /** 제목이나 본문이 없거나 공백뿐이면 알림을 만들지 않는지 검증한다. */
    @Test
    fun toNotificationContent_필수값이비어있으면_null을반환한다() {
        val invalidPayloads =
            listOf(
                emptyMap(),
                mapOf("title" to "제목"),
                mapOf("body" to "본문"),
                mapOf("title" to " ", "body" to "본문"),
                mapOf("title" to "제목", "body" to " "),
            )

        invalidPayloads.forEach { payload -> assertNull(payload.toNotificationContent(), payload.toString()) }
    }

    /** data payload가 유효하면 notification payload보다 우선하는지 검증한다. */
    @Test
    fun resolveNotificationContent_data가유효하면_data를우선한다() {
        val content =
            resolveNotificationContent(
                data = mapOf("title" to "data 제목", "body" to "data 본문"),
                notificationTitle = "notification 제목",
                notificationBody = "notification 본문",
            )

        assertEquals(NotificationContent("data 제목", "data 본문"), content)
    }

    /** data payload가 유효하지 않으면 notification payload로 대체하는지 검증한다. */
    @Test
    fun resolveNotificationContent_data가유효하지않으면_notification으로대체한다() {
        val content =
            resolveNotificationContent(
                data = emptyMap(),
                notificationTitle = " notification 제목 ",
                notificationBody = " notification 본문 ",
            )

        assertEquals(NotificationContent("notification 제목", "notification 본문"), content)
    }

    /** 알림이 활성화되면 정리한 FID를 기기 ID와 푸시 토큰으로 사용하는지 검증한다. */
    @Test
    fun createAndroidDeviceInfo_알림이활성화되면_fid를토큰으로사용한다() {
        val deviceInfo =
            createAndroidDeviceInfo(
                installationId = " firebase-installation-id ",
                appVersion = "1.0",
                osVersion = "16",
                notificationsEnabled = true,
            )

        assertEquals("firebase-installation-id", deviceInfo?.deviceId)
        assertEquals("firebase-installation-id", deviceInfo?.deviceToken)
        assertEquals("android", deviceInfo?.deviceType)
    }

    /** 알림이 비활성화되면 기기 ID는 유지하고 푸시 토큰만 제외하는지 검증한다. */
    @Test
    fun createAndroidDeviceInfo_알림이비활성화되면_토큰을제외한다() {
        val deviceInfo =
            createAndroidDeviceInfo(
                installationId = "firebase-installation-id",
                appVersion = "1.0",
                osVersion = "16",
                notificationsEnabled = false,
            )

        assertEquals("firebase-installation-id", deviceInfo?.deviceId)
        assertNull(deviceInfo?.deviceToken)
    }
}
