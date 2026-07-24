package com.nexters.hytime.gitit.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.chunked
import co.touchlab.kermit.platformLogWriter
import org.koin.core.module.Module
import org.koin.dsl.module

fun initLogger(isDebug: Boolean) {
    Logger.setLogWriters(platformLogWriter().chunked())
    if (!isDebug) Logger.setMinSeverity(Severity.Assert)
}

fun gitItLogger(tag: String = "GitIt"): AppLogger = AppLogger(Logger.withTag(tag))

class AppLogger internal constructor(
    private val delegate: Logger,
) {
    fun d(
        throwable: Throwable? = null,
        message: () -> String,
    ) = delegate.d(throwable = throwable) { callerLocation() + message() }

    fun i(
        throwable: Throwable? = null,
        message: () -> String,
    ) = delegate.i(throwable = throwable) { callerLocation() + message() }

    fun w(
        throwable: Throwable? = null,
        message: () -> String,
    ) = delegate.w(throwable = throwable) { callerLocation() + message() }

    fun e(
        throwable: Throwable? = null,
        message: () -> String,
    ) = delegate.e(throwable = throwable) { callerLocation() + message() }
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
