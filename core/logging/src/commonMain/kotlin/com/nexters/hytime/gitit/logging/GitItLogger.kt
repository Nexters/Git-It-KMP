package com.nexters.hytime.gitit.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.chunked
import co.touchlab.kermit.platformLogWriter

private class CallerLocationLogWriter(
    private val delegate: LogWriter,
) : LogWriter() {
    override fun isLoggable(
        tag: String,
        severity: Severity,
    ): Boolean = delegate.isLoggable(tag, severity)

    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        delegate.log(severity, message, tag, throwable)
    }

}

fun initLogger() {
    Logger.setLogWriters(CallerLocationLogWriter(platformLogWriter().chunked()))
}

fun gitItLogger(tag: String = "GitIt"): Logger = Logger.withTag(tag)
