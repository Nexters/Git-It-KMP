package com.nexters.hytime.gitit

import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.loggingModule
import com.nexters.hytime.gitit.network.di.networkModule
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module =
    module {
        single<NetworkLogger> {
            val logger = gitItLogger(tag = "🌐 Network")
            NetworkLogger { message -> logger.d { message } }
        }
        single { Greeting(get()) }
    }

val appModules: List<Module> = listOf(loggingModule, networkModule, appModule)
