package com.nexters.hytime.gitit

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.nexters.hytime.gitit.auth.DesktopGoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthTokenProvider
import com.nexters.hytime.gitit.auth.GoogleAuthenticator
import com.nexters.hytime.gitit.auth.InMemoryLoginSessionStorage
import com.nexters.hytime.gitit.data.di.dataModule
import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.feature.onboarding.onboardingModule
import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.initLogger
import com.nexters.hytime.gitit.network.di.networkModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main() {
    initLogger(isDebug = true)
    val logger = gitItLogger(tag = "🌐 Network")
    startKoin {
        modules(
            appModules +
                onboardingModule +
                dataModule +
                platformModule +
                networkModule(
                    networkLogger = { message -> logger.d { message } },
                    baseUrl = AuthConfig.BACKEND_BASE_URL,
                ),
        )
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

private val platformModule =
    module {
        single<GoogleAuthenticator> {
            DesktopGoogleAuthenticator(
                clientId = AuthConfig.GOOGLE_DESKTOP_CLIENT_ID,
                clientSecret = AuthConfig.GOOGLE_DESKTOP_CLIENT_SECRET,
            )
        }
        single<AuthTokenProvider> { GoogleAuthTokenProvider(get()) }
        single<LoginSessionStorage> { InMemoryLoginSessionStorage() }
    }
