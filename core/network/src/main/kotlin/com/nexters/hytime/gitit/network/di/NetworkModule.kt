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
 * @param accessTokenProvider 현재 로그인 세션의 액세스 토큰을 제공한다
 * @return 네트워크 클라이언트를 등록한 Koin 모듈
 */
fun networkModule(
    networkLogger: NetworkLogger,
    baseUrl: String,
    accessTokenProvider: suspend () -> String?,
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
                accessTokenProvider = accessTokenProvider,
            )
        }
    }
