package com.nexters.hytime.gitit

import android.content.Context
import android.app.Application
import com.nexters.hytime.gitit.BuildConfig
import com.nexters.hytime.gitit.auth.AndroidGoogleAuthenticatorFactory
import com.nexters.hytime.gitit.auth.GoogleAuthenticatorFactory
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
 * Web Client ID로 [AndroidGoogleAuthenticatorFactory]를 생성한다.
 */
private val platformModule =
    module {
        single<GoogleAuthenticatorFactory> {
            AndroidGoogleAuthenticatorFactory(
                context = get<Context>(),
                serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
            )
        }
    }
