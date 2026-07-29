package com.nexters.hytime.gitit.data.di

import com.nexters.hytime.gitit.data.repository.AccountRepositoryImpl
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.network.api.NetworkClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * `data` 레이어의 Koin DI 모듈이다.
 *
 * [AccountRepository] 인터페이스에 [AccountRepositoryImpl] 구현체를 바인딩한다.
 * [NetworkClient]와 [Json]은 상위 모듈(`networkModule`)에서 이미 제공된다고 가정한다.
 */
val dataModule =
    module {
        single {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
        single<AccountRepository> {
            AccountRepositoryImpl(
                networkClient = get(),
                baseUrl = get(named("baseUrl")),
                json = get(),
            )
        }
    }
