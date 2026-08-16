package com.nexters.hytime.gitit.data.di

import com.nexters.hytime.gitit.data.repository.AuthRepositoryImpl
import com.nexters.hytime.gitit.data.repository.GitHubRepositoryRepositoryImpl
import com.nexters.hytime.gitit.data.repository.MemberRepositoryImpl
import com.nexters.hytime.gitit.data.repository.ProjectRepositoryImpl
import com.nexters.hytime.gitit.domain.repository.AuthRepository
import com.nexters.hytime.gitit.domain.repository.GitHubRepositoryRepository
import com.nexters.hytime.gitit.domain.repository.MemberRepository
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.network.api.NetworkClient
import org.koin.dsl.module

val dataModule =
    module {
        single<AuthRepository> {
            AuthRepositoryImpl(networkClient = get<NetworkClient>())
        }
        single<MemberRepository> {
            MemberRepositoryImpl(networkClient = get<NetworkClient>())
        }
        single<ProjectRepository> {
            ProjectRepositoryImpl(networkClient = get<NetworkClient>())
        }
        single<GitHubRepositoryRepository> {
            GitHubRepositoryRepositoryImpl(networkClient = get<NetworkClient>())
        }
    }
