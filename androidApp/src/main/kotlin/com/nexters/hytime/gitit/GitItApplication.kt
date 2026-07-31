package com.nexters.hytime.gitit

import android.content.Context
import android.app.Application
import com.nexters.hytime.gitit.BuildConfig
import com.nexters.hytime.gitit.auth.AndroidGoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthTokenProvider
import com.nexters.hytime.gitit.data.di.dataModule
import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
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
        startKoin {
            androidContext(this@GitItApplication)
            modules(
                appModules +
                    dataModule +
                    platformModule +
                    networkModule(
                        networkLogger = { message -> logger.d { message } },
                        baseUrl = BuildConfig.BACKEND_BASE_URL,
                    ),
            )
        }
    }
}

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
