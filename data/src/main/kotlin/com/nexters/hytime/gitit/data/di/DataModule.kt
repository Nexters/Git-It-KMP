package com.nexters.hytime.gitit.data.di

import com.nexters.hytime.gitit.data.repository.AccountRepositoryImpl
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.network.api.NetworkClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * `data` 레이어의 Koin DI 모듈을 생성한다.
 *
 * [AccountRepository] 인터페이스에 [AccountRepositoryImpl] 구현체를 바인딩한다.
 * 백엔드 기준 URL은 composition root(플랫폼 앱)에서 주입받아 named qualifier 없이 직접 전달한다.
 *
 * @param baseUrl 백엔드 API 기준 URL
 * @return 조립된 Koin 모듈
 */
fun dataModule(baseUrl: String): org.koin.core.module.Module =
    module {
        single {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
        single<AccountRepository> {
            AccountRepositoryImpl(
                networkClient = get<NetworkClient>(),
                baseUrl = baseUrl,
                json = get(),
            )
        }
    }
