package com.nexters.hytime.gitit

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.nexters.hytime.gitit.auth.DesktopGoogleAuthenticatorFactory
import com.nexters.hytime.gitit.auth.GoogleAuthenticatorFactory
import com.nexters.hytime.gitit.data.di.dataModule
import com.nexters.hytime.gitit.logging.initLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * 데스크톱 애플리케이션 진입점이다.
 *
 * Kermit 로거를 초기화하고 Koin DI를 시작한 뒤, Compose Desktop 윈도우를 띄운다.
 */
fun main() {
    initLogger(isDebug = true)
    startKoin {
        modules(appModules + dataModule(AuthConfig.BACKEND_BASE_URL) + platformModule)
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

/**
 * 데스크톱 플랫폼 전용 DI 바인딩이다.
 *
 * Google "Desktop 앱" 유형 OAuth 클라이언트 ID로 [DesktopGoogleAuthenticatorFactory]를 생성한다.
 * 설정 값은 빌드 시점에 생성되는 [AuthConfig]에서 가져온다.
 */
private val platformModule =
    module {
        single<GoogleAuthenticatorFactory> {
            DesktopGoogleAuthenticatorFactory(
                clientId = AuthConfig.GOOGLE_DESKTOP_CLIENT_ID,
                clientSecret = AuthConfig.GOOGLE_DESKTOP_CLIENT_SECRET,
            )
        }
    }
