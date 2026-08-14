package com.nexters.hytime.gitit

import android.app.Application
import android.content.Context
import com.nexters.hytime.gitit.auth.AndroidLoginSessionStorage
import com.nexters.hytime.gitit.auth.AndroidGoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthTokenProvider
import com.nexters.hytime.gitit.data.di.dataModule
import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.feature.onboarding.onboardingModule
import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.initLogger
import com.nexters.hytime.gitit.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class GitItApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger(BuildConfig.DEBUG)
        val logger = gitItLogger(tag = "🌐 Network")
        val sessionStorage = AndroidLoginSessionStorage(this)
        startKoin {
            androidContext(this@GitItApplication)
            modules(
                appModules +
                    onboardingModule +
                    dataModule +
                    platformModule(sessionStorage) +
                    networkModule(
                        networkLogger = { message -> logger.d { message } },
                        baseUrl = BuildConfig.BACKEND_BASE_URL,
                        accessTokenProvider = { sessionStorage.load()?.accessToken },
                    ),
            )
        }
    }
}

/**
 * Android 인증 의존성을 등록한다.
 *
 * @param sessionStorage 네트워크 인증과 로그인에서 공유할 세션 저장소
 * @return Android 인증 의존성이 등록된 Koin 모듈
 */
private fun platformModule(sessionStorage: LoginSessionStorage) =
    module {
        single<GoogleAuthenticator> {
            AndroidGoogleAuthenticator(
                context = get<Context>(),
                serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
            )
        }
        single<AuthTokenProvider> { GoogleAuthTokenProvider(get()) }
        single<LoginSessionStorage> { sessionStorage }
    }
