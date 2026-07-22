package com.nexters.hytime.gitit.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.chunked
import co.touchlab.kermit.platformLogWriter

fun initLogger() {
    Logger.setLogWriters(platformLogWriter().chunked())
}

fun gitItLogger(tag: String = "GitIt"): Logger = Logger.withTag(tag)

/** 로깅 내부 프레임을 건너뛰고 호출자의 `(File.kt:line)` 를 찾는다. 반드시 non-inline 함수에서 호출. */
private fun callerLocation(): String {
    for (frame in Throwable().stackTrace) {
        if (frame.fileName == "GitItLogger.kt") continue
        val cls = frame.className
        if (cls.startsWith("co.touchlab.kermit")) continue
        if (cls.startsWith("java.") || cls.startsWith("jdk.") || cls.startsWith("kotlin.")) continue
        val file = frame.fileName?.takeUnless { it.isNullOrBlank() || it == "<unknown>" } ?: continue
        return "($file:${frame.lineNumber}) "
    }
    return ""
}

fun Logger.logD(
    throwable: Throwable? = null,
    message: () -> String,
) = d(throwable = throwable) { callerLocation() + message() }

fun Logger.logI(
    throwable: Throwable? = null,
    message: () -> String,
) = i(throwable = throwable) { callerLocation() + message() }

fun Logger.logW(
    throwable: Throwable? = null,
    message: () -> String,
) = w(throwable = throwable) { callerLocation() + message() }

fun Logger.logE(
    throwable: Throwable? = null,
    message: () -> String,
) = e(throwable = throwable) { callerLocation() + message() }
