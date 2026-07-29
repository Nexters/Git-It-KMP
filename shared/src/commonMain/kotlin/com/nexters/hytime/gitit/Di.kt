package com.nexters.hytime.gitit

import com.nexters.hytime.gitit.auth.GoogleAuthTokenProvider
import com.nexters.hytime.gitit.auth.GoogleAuthenticatorFactory
import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.usecase.SignInUseCase
import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.loggingModule
import com.nexters.hytime.gitit.network.di.networkModule
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 앱 공통 DI 모듈이다.
 *
 * 네트워크 로거, Greeting, [SignInUseCase]를 등록한다.
 * 플랫폼 제공 의존성([GoogleAuthenticatorFactory], 백엔드 URL)은
 * 각 플랫폼 모듈에서 주입된다.
 */
val appModule: Module =
    module {
        single<NetworkLogger> {
            val logger = gitItLogger(tag = "🌐 Network")
            NetworkLogger { message -> logger.d { message } }
        }
        single { Greeting(get()) }
        single<AuthTokenProvider> {
            GoogleAuthTokenProvider(get<GoogleAuthenticatorFactory>().create())
        }
        single {
            SignInUseCase(
                tokenProvider = get<AuthTokenProvider>(),
                accountRepository = get<AccountRepository>(),
            )
        }
    }

/**
 * 앱 전체 Koin 모듈 목록이다.
 *
 * [loggingModule], [networkModule], [appModule]을 조합한다.
 * `dataModule`은 composition root(플랫폼 앱)에서 URL과 함께 등록한다.
 */
val appModules: List<Module> = listOf(loggingModule, networkModule, appModule)
