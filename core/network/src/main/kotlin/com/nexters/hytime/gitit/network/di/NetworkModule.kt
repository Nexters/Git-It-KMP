package com.nexters.hytime.gitit.network.di

import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.http.KtorNetworkClient
import com.nexters.hytime.gitit.network.http.createGitItHttpClient
import com.nexters.hytime.gitit.network.logging.NetworkLogger
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 네트워크 의존성을 제공하는 Koin 모듈을 생성한다.
 *
 * @param networkLogger HTTP 로거
 * @param baseUrl 모든 API 요청의 기준 URL
 */
fun networkModule(
    networkLogger: NetworkLogger,
    baseUrl: String,
): Module =
    module {
        single<NetworkClient> {
            KtorNetworkClient(
                client = createGitItHttpClient(networkLogger),
                json =
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                baseUrl = baseUrl,
            )
        }
    }
