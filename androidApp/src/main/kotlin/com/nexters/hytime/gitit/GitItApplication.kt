package com.nexters.hytime.gitit

import android.app.Application
import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.initLogger
import com.nexters.hytime.gitit.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GitItApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger(BuildConfig.DEBUG)
        val logger = gitItLogger(tag = "🌐 Network")
        startKoin {
            androidContext(this@GitItApplication)
            modules(appModules + networkModule { message -> logger.d { message } })
        }
    }
}
