package com.nexters.hytime.gitit

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Git-It-Android",
    ) {
        App()
    }
}