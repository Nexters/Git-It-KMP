package com.nexters.hytime.gitit

import android.content.Context
import android.app.Application
import com.nexters.hytime.gitit.BuildConfig
import com.nexters.hytime.gitit.auth.AndroidGoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.data.di.dataModule
import com.nexters.hytime.gitit.logging.initLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class GitItApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger(BuildConfig.DEBUG)
        startKoin {
            androidContext(this@GitItApplication)
            modules(appModules + dataModule(BuildConfig.BACKEND_BASE_URL) + platformModule)
        }
    }
}

/**
 * Android 플랫폼 전용 DI 바인딩이다.
 *
 * Credential Manager에 필요한 [Context]와 백엔드 검증용
 * Web Client ID로 [AndroidGoogleAuthenticator]를 생성하고, 이를
 * [AuthTokenProvider] 포트에 [GoogleAuthTokenProvider] 어댑터로 연결한다.
 */
private val platformModule =
    module {
        single<GoogleAuthenticator> {
            AndroidGoogleAuthenticator(
                context = get<Context>(),
                serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
            )
        }
        single<AuthTokenProvider> { GoogleAuthTokenProvider(get()) }
    }
