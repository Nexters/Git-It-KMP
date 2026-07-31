package com.nexters.hytime.gitit.network.di

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.http.KtorNetworkClient
import com.nexters.hytime.gitit.network.http.createGitItHttpClient
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Git-It 네트워크 의존성을 제공하는 Koin 모듈을 생성한다.
 *
 * @param networkLogger HTTP 통신 로그를 전달할 로거
 * @return 네트워크 클라이언트 의존성을 제공하는 Koin 모듈
 */
fun networkModule(networkLogger: NetworkLogger): Module =
    module {
        single<NetworkClient> { KtorNetworkClient(createGitItHttpClient(networkLogger)) }
    }
