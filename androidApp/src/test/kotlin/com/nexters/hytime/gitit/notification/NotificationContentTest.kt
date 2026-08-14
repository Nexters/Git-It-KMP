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
}
