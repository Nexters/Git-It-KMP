package com.nexters.hytime.gitit.data.di

import com.nexters.hytime.gitit.data.repository.AccountRepositoryImpl
import com.nexters.hytime.gitit.data.repository.GitHubRepositoryRepositoryImpl
import com.nexters.hytime.gitit.data.repository.ProjectRepositoryImpl
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.repository.GitHubRepositoryRepository
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.network.api.NetworkClient
import org.koin.dsl.module

val dataModule =
    module {
        single<AccountRepository> {
            AccountRepositoryImpl(networkClient = get<NetworkClient>())
        }
        single<GitHubRepositoryRepository> {
            GitHubRepositoryRepositoryImpl(networkClient = get<NetworkClient>())
        }
        single<ProjectRepository> {
            ProjectRepositoryImpl(networkClient = get<NetworkClient>())
        }
    }
