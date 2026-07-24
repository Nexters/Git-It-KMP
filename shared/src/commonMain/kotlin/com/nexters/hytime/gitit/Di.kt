package com.nexters.hytime.gitit

import com.nexters.hytime.gitit.logging.loggingModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module =
    module {
        single { Greeting(get()) }
    }

val appModules: List<Module> = listOf(loggingModule, appModule)
