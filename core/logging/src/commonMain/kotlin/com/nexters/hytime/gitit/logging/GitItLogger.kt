package com.nexters.hytime.gitit.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.chunked
import co.touchlab.kermit.platformLogWriter
import io.kotzilla.sdk.KotzillaCore
import org.koin.core.module.Module
import org.koin.dsl.module

fun initLogger(isDebug: Boolean) {
    Logger.setLogWriters(platformLogWriter().chunked())
    if (!isDebug) Logger.setMinSeverity(Severity.Assert)
}

fun gitItLogger(tag: String = "GitIt"): AppLogger = AppLogger(Logger.withTag(tag))

/**
 * 로컬 로그를 기록하고 처리된 비정상 예외를 운영 관측 도구의 타임라인과 이슈로 전달한다.
 *
 * @property delegate 플랫폼별 로그 출력에 사용하는 Kermit 로거
 * @property reportError 처리된 예외를 원격 타임라인과 이슈에 기록하는 함수
 */
class AppLogger internal constructor(
    private val delegate: Logger,
    private val reportError: (String, Throwable) -> Unit = { message, error ->
        KotzillaCore.getDefaultInstance().run {
            logError(message, error)
            createIssue(message.take(256), error.toString().take(256))
        }
    },
) {
    fun d(
        throwable: Throwable? = null,
        message: () -> String,
    ) = delegate.d(throwable = throwable) { callerLocation() + message() }

    fun i(
        throwable: Throwable? = null,
        message: () -> String,
    ) = delegate.i(throwable = throwable) { callerLocation() + message() }

    /**
     * 복구 가능한 경고를 기록하고 예외가 있으면 운영 타임라인과 이슈에도 보고한다.
     *
     * @param throwable 원인을 추적할 예외. 없으면 원격으로 전송하지 않는다
     * @param message 개인정보를 포함하지 않는 기술적 상황 설명
     */
    fun w(
        throwable: Throwable? = null,
        message: () -> String,
    ) {
        val formattedMessage = callerLocation() + message()
        delegate.w(throwable = throwable) { formattedMessage }
        report(formattedMessage, throwable)
    }

    /**
     * 처리된 오류를 기록하고 예외가 있으면 운영 타임라인과 이슈에도 보고한다.
     *
     * @param throwable 원인을 추적할 예외. 없으면 원격으로 전송하지 않는다
     * @param message 개인정보를 포함하지 않는 기술적 상황 설명
     */
    fun e(
        throwable: Throwable? = null,
        message: () -> String,
    ) {
        val formattedMessage = callerLocation() + message()
        delegate.e(throwable = throwable) { formattedMessage }
        report(formattedMessage, throwable)
    }

    /**
     * 원격 관측 실패가 앱의 오류 처리 흐름을 깨뜨리지 않도록 안전하게 예외를 전달한다.
     *
     * @param message 호출 위치가 포함된 기술적 오류 설명
     * @param throwable 원격으로 전달할 예외. 없으면 아무 작업도 하지 않는다
     */
    private fun report(
        message: String,
        throwable: Throwable?,
    ) {
        throwable?.let { error -> runCatching { reportError(message, error) } }
    }
}

/** 로깅 내부 프레임을 건너뛰고 호출자의 `(File.kt:line)` 를 찾는다. */
private fun callerLocation(): String {
    for (frame in Throwable().stackTrace) {
        if (frame.fileName == "GitItLogger.kt") continue
        val cls = frame.className
        if (cls.startsWith("co.touchlab.kermit")) continue
        if (cls.startsWith("java.") || cls.startsWith("jdk.") || cls.startsWith("kotlin.")) continue
        val file = frame.fileName?.takeUnless { it.isBlank() || it == "<unknown>" } ?: continue
        return "($file:${frame.lineNumber}) "
    }
    return ""
}

val loggingModule: Module =
    module {
        single { gitItLogger() }
    }
