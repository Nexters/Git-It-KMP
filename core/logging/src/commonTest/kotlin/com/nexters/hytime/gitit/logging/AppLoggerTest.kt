package com.nexters.hytime.gitit.logging

import co.touchlab.kermit.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** [AppLogger]의 원격 오류 전송 조건을 검증한다. */
class AppLoggerTest {
    /** 예외가 포함된 오류는 메시지와 원인을 원격 보고 함수에 전달한다. */
    @Test
    fun e_withThrowable_reportsRemoteFailure() {
        val error = IllegalStateException("실패")
        var reportedMessage: String? = null
        var reportedError: Throwable? = null
        val logger =
            AppLogger(Logger.withTag("Test")) { message, throwable ->
                reportedMessage = message
                reportedError = throwable
            }

        logger.e(throwable = error) { "원격 오류" }

        assertTrue(reportedMessage?.endsWith("원격 오류") == true)
        assertSame(error, reportedError)
    }

    /** 예외가 없는 경고는 로컬에만 기록하고 원격 보고 함수를 호출하지 않는다. */
    @Test
    fun w_withoutThrowable_doesNotReportRemoteError() {
        var reportCount = 0
        val logger = AppLogger(Logger.withTag("Test")) { _, _ -> reportCount++ }

        logger.w { "로컬 경고" }

        assertEquals(0, reportCount)
    }
}
