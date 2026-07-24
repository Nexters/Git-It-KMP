package com.nexters.hytime.gitit

import android.app.Application
import com.nexters.hytime.gitit.BuildConfig
import com.nexters.hytime.gitit.logging.initLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GitItApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger(BuildConfig.DEBUG)
        startKoin {
            androidContext(this@GitItApplication)
            modules(appModules)
        }
    }
}
