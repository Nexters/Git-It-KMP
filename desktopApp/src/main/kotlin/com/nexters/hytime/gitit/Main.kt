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
    val sessionStorage = InMemoryLoginSessionStorage()
    startKoin {
        modules(
            appModules +
                onboardingModule +
                dataModule +
                platformModule(sessionStorage) +
                networkModule(
                    networkLogger = { message -> logger.d { message } },
                    baseUrl = AuthConfig.BACKEND_BASE_URL,
                    accessTokenProvider = { sessionStorage.load()?.accessToken },
                ),
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Git-It",
        ) {
            App()
        }
    }
}

/**
 * Desktop 인증 의존성을 등록한다.
 *
 * @param sessionStorage 네트워크 인증과 로그인에서 공유할 세션 저장소
 * @return Desktop 인증 의존성이 등록된 Koin 모듈
 */
private fun platformModule(sessionStorage: LoginSessionStorage) =
    module {
        single<GoogleAuthenticator> {
            DesktopGoogleAuthenticator(
                clientId = AuthConfig.GOOGLE_DESKTOP_CLIENT_ID,
                clientSecret = AuthConfig.GOOGLE_DESKTOP_CLIENT_SECRET,
            )
        }
        single<AuthTokenProvider> { GoogleAuthTokenProvider(get()) }
        single<LoginSessionStorage> { sessionStorage }
    }
