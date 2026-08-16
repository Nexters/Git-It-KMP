package com.nexters.hytime.gitit.feature.quiz.load

import com.nexters.hytime.gitit.domain.model.GitHubRepository
import com.nexters.hytime.gitit.domain.repository.GitHubRepositoryRepository
import com.nexters.hytime.gitit.domain.usecase.LoadGitHubRepositoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** [ProjectLoadViewModel]의 입력, 조회, 확인 상태 전환을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class ProjectLoadViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    /** ViewModel의 Main dispatcher를 테스트 dispatcher로 교체한다. */
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    /** 테스트 이후 Main dispatcher를 복원한다. */
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** 올바르지 않은 URL을 제출하면 형식 오류를 표시하는지 검증한다. */
    @Test
    fun loadRepository_invalidUrlShowsFormatError() =
        runTest(dispatcher) {
            val viewModel = viewModel(Result.success(repository))
            viewModel.onIntent(ProjectLoadIntent.RepositoryUrlChanged("https://example.com/repo"))

            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(ProjectLoadError.InvalidUrl, viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isLoading)
        }

    /** 조회 성공 시 확인할 저장소를 상태에 저장하는지 검증한다. */
    @Test
    fun loadRepository_successShowsRepositoryConfirmation() =
        runTest(dispatcher) {
            val viewModel = viewModel(Result.success(repository))
            viewModel.onIntent(ProjectLoadIntent.RepositoryUrlChanged("https://github.com/facebook/react"))

            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(repository, viewModel.uiState.value.repository)
            assertNull(viewModel.uiState.value.error)
        }

    /** 디자인에 없는 오류를 노출하지 않고 API 실패 후 입력 화면을 다시 조작할 수 있다. */
    @Test
    fun loadRepository_apiFailureReturnsToEditableInput() =
        runTest(dispatcher) {
            val viewModel = viewModel(Result.failure(IllegalStateException("network")))
            viewModel.onIntent(ProjectLoadIntent.RepositoryUrlChanged("https://github.com/facebook/react"))

            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
            dispatcher.scheduler.advanceUntilIdle()

            assertNull(viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isLoading)
        }

    /** 조회 중 재요청을 무시하는지 검증한다. */
    @Test
    fun loadRepository_whileLoadingIgnoresDuplicateRequest() =
        runTest(dispatcher) {
            var calls = 0
            val viewModel =
                ProjectLoadViewModel(
                    LoadGitHubRepositoryUseCase(
                        repository { _, _ ->
                            calls += 1
                            Result.success(repository)
                        },
                    ),
                )
            viewModel.onIntent(ProjectLoadIntent.RepositoryUrlChanged("https://github.com/facebook/react"))

            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, calls)
        }

    /** 저장소 거절 시 URL을 유지하고 입력 단계로 돌아가는지 검증한다. */
    @Test
    fun rejectRepository_keepsUrlAndReturnsToInput() =
        runTest(dispatcher) {
            val viewModel = viewModel(Result.success(repository))
            val url = "https://github.com/facebook/react"
            viewModel.onIntent(ProjectLoadIntent.RepositoryUrlChanged(url))
            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
            dispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(ProjectLoadIntent.RejectRepository)

            assertEquals(url, viewModel.uiState.value.repositoryUrl)
            assertNull(viewModel.uiState.value.repository)
        }

    /** 저장소 확인 시 확인 이벤트에 저장소 정보를 담는지 검증한다. */
    @Test
    fun confirmRepository_emitsConfirmedRepository() =
        runTest(dispatcher) {
            val viewModel = viewModel(Result.success(repository))
            viewModel.onIntent(ProjectLoadIntent.RepositoryUrlChanged("https://github.com/facebook/react"))
            viewModel.onIntent(ProjectLoadIntent.LoadRepository)
            dispatcher.scheduler.advanceUntilIdle()
            val event = async { viewModel.events.first() }

            viewModel.onIntent(ProjectLoadIntent.ConfirmRepository)
            dispatcher.scheduler.advanceUntilIdle()

            assertTrue(event.await() is ProjectLoadEvent.RepositoryConfirmed)
            assertEquals(repository, (event.await() as ProjectLoadEvent.RepositoryConfirmed).repository)
        }

    private fun viewModel(result: Result<GitHubRepository>): ProjectLoadViewModel =
        ProjectLoadViewModel(LoadGitHubRepositoryUseCase(repository { _, _ -> result }))

    private fun repository(block: suspend (String, String) -> Result<GitHubRepository>): GitHubRepositoryRepository =
        object : GitHubRepositoryRepository {
            override suspend fun getRepository(
                owner: String,
                name: String,
            ): Result<GitHubRepository> = block(owner, name)
        }

    private companion object {
        private val repository =
            GitHubRepository(
                name = "react",
                ownerName = "facebook",
                ownerAvatarUrl = "https://example.com/avatar.png",
            )
    }
}
