package com.nexters.hytime.gitit

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.initLogger
import com.nexters.hytime.gitit.network.di.networkModule
import org.koin.core.context.startKoin

fun main() {
    initLogger(isDebug = true)
    val logger = gitItLogger(tag = "🌐 Network")
    startKoin {
        modules(appModules + networkModule { message -> logger.d { message } })
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Git-It-Android",
        ) {
            App()
        }
    }
}
