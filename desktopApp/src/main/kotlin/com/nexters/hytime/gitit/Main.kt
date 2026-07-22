package com.nexters.hytime.gitit

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.nexters.hytime.gitit.logging.initLogger
import org.koin.core.context.startKoin

fun main() =
    application {
        initLogger()
        startKoin {
            modules(appModules)
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = "Git-It-Android",
        ) {
            App()
        }
    }
