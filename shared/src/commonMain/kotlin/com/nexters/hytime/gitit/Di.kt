package com.nexters.hytime.gitit

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.repository.AuthRepository
import com.nexters.hytime.gitit.domain.repository.GitHubRepositoryRepository
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.domain.usecase.LoadGitHubRepositoryUseCase
import com.nexters.hytime.gitit.domain.usecase.RegisterProjectUseCase
import com.nexters.hytime.gitit.domain.usecase.SignInUseCase
import com.nexters.hytime.gitit.feature.projectdetail.ProjectDetailViewModel
import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateViewModel
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateRetryHandler
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStore
import com.nexters.hytime.gitit.feature.quiz.load.ProjectLoadViewModel
import com.nexters.hytime.gitit.logging.loggingModule
import com.nexters.hytime.gitit.presentation.splash.SplashViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * 앱 공통 DI 모듈이다.
 *
 * 네트워크 로거와 화면에서 사용하는 UseCase·ViewModel을 등록한다.
 * 플랫폼 제공 의존성([AuthTokenProvider], 백엔드 URL)은
 * 각 플랫폼 composition root에서 주입된다.
 */
val appModule: Module =
    module {
        single {
            SignInUseCase(
                tokenProvider = get<AuthTokenProvider>(),
                authRepository = get<AuthRepository>(),
                sessionStorage = get<LoginSessionStorage>(),
            )
        }
        single { LoadGitHubRepositoryUseCase(repository = get<GitHubRepositoryRepository>()) }
        single { RegisterProjectUseCase(repository = get<ProjectRepository>()) }
        viewModel { SplashViewModel(authRepository = get(), sessionStorage = get()) }
        viewModel { params -> ProjectDetailViewModel(projectId = params.get<String>()) }
        viewModel { ProjectLoadViewModel(loadGitHubRepository = get()) }
        single { QuizCreateStore() }
        single { QuizCreateRetryHandler(registerProject = get(), createStore = get()) }
        viewModel { params ->
            QuizCreateViewModel(
                repositoryUrl = params.get<String>(),
                registerProject = get(),
                createStore = get(),
            )
        }
    }

/**
 * 앱 전체 Koin 모듈 목록이다.
 *
 * [loggingModule], [appModule]을 조합한다.
 * `dataModule`은 composition root(플랫폼 앱)에서 URL과 함께 등록한다.
 */
val appModules: List<Module> = listOf(loggingModule, appModule)
