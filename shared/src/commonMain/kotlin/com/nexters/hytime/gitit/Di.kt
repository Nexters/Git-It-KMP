package com.nexters.hytime.gitit

import com.nexters.hytime.gitit.auth.GoogleAuthenticatorFactory
import com.nexters.hytime.gitit.data.di.dataModule
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.usecase.SignInWithGoogleUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.loggingModule
import com.nexters.hytime.gitit.network.di.networkModule
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule: Module =
    module {
        single<NetworkLogger> {
            val logger = gitItLogger(tag = "🌐 Network")
            NetworkLogger { message -> logger.d { message } }
        }
        single { Greeting(get()) }
        single {
            SignInWithGoogleUseCase(
                authenticator = get<GoogleAuthenticatorFactory>().create(),
                accountRepository = get<AccountRepository>(),
            )
        }
    }

val appModules: List<Module> = listOf(loggingModule, networkModule, dataModule, appModule)

/** DI 식별자 — 백엔드 기준 URL 바인딩용 */
val baseUrlQualifier = named("baseUrl")
