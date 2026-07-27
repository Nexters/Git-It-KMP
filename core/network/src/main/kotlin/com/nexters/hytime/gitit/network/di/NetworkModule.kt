package com.nexters.hytime.gitit.network.di

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.http.KtorNetworkClient
import com.nexters.hytime.gitit.network.http.createGitItHttpClient
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import org.koin.core.module.Module
import org.koin.dsl.module

/** Git-It 네트워크 의존성을 제공하는 Koin 모듈이다. */
val networkModule: Module =
    module {
        single<NetworkClient> { KtorNetworkClient(createGitItHttpClient(get())) }
    }
