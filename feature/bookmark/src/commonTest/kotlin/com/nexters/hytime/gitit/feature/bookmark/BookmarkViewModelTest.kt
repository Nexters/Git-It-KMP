@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.bookmark

import com.nexters.hytime.gitit.domain.model.AvailableProject
import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.usecase.BookmarkQuestionUseCase
import com.nexters.hytime.gitit.domain.usecase.GetBookmarkedQuestionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import com.nexters.hytime.gitit.domain.model.BookmarkedQuestion as DomainBookmarkedQuestion

/** 저장한 문제 ViewModel의 목록 조회와 북마크 상태 전환을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {
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

    /** 목록 조회가 성공하면 전체 필터 뒤에 프로젝트 필터를 잇고 문제 카드를 채운다. */
    @Test
    fun init_목록조회에성공하면_필터와문제를채운다() {
        runTest(dispatcher) {
            val repository = FakeBookmarkRepository(Result.success(BOOKMARKS))
            val viewModel = createViewModel(repository)
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(null, repository.requestedProjectId)
            assertEquals(listOf("all", "p1"), state.filters.map(BookmarkFilter::id))
            assertEquals(listOf(null, "flask"), state.filters.map(BookmarkFilter::label))
            assertEquals("all", state.selectedFilterId)
            assertEquals(listOf("q1"), state.questions.map(BookmarkedQuestion::id))
            assertEquals("flask", state.questions.first().projectName)
            assertEquals("Set 1", state.questions.first().setLabel)
            assertEquals(3, state.questions.first().problemNumber)
            assertEquals("블루프린트의 목적은?", state.questions.first().title)
        }
    }

    /** 프로젝트 필터를 선택하면 해당 프로젝트로 다시 조회한다. */
    @Test
    fun filterClick_프로젝트필터를선택하면_프로젝트로다시조회한다() {
        runTest(dispatcher) {
            val repository = FakeBookmarkRepository(Result.success(BOOKMARKS))
            val viewModel = createViewModel(repository)
            runCurrent()

            viewModel.onIntent(BookmarkIntent.FilterClick("p1"))
            runCurrent()

            assertEquals("p1", repository.requestedProjectId)
            assertEquals(2, repository.loadCount)
            assertEquals("p1", viewModel.uiState.value.selectedFilterId)
        }
    }

    /** 저장된 문제를 반복 선택하면 해제 상태와 저장 상태를 차례로 반영한다. */
    @Test
    fun onIntent_bookmarkClick_togglesBookmarkState() {
        runTest(dispatcher) {
            val repository = FakeBookmarkRepository(Result.success(BOOKMARKS))
            val viewModel = createViewModel(repository)
            runCurrent()
            val questionId =
                viewModel.uiState.value.questions
                    .first()
                    .id

            assertEquals(emptyMap(), viewModel.uiState.value.bookmarkChanges)

            viewModel.onIntent(BookmarkIntent.BookmarkClick(questionId))
            runCurrent()
            assertEquals(mapOf(questionId to false), viewModel.uiState.value.bookmarkChanges)
            assertEquals("p1", repository.bookmarkedProjectId)
            assertEquals(questionId, repository.bookmarkedQuestionId)
            assertEquals(false, repository.requestedBookmarked)

            viewModel.onIntent(BookmarkIntent.BookmarkClick(questionId))
            runCurrent()
            assertEquals(mapOf(questionId to true), viewModel.uiState.value.bookmarkChanges)
        }
    }

    /** 북마크 설정이 실패하면 표시 상태를 유지한다. */
    @Test
    fun bookmarkClick_설정이실패하면_표시상태를유지한다() {
        runTest(dispatcher) {
            val repository = FakeBookmarkRepository(Result.success(BOOKMARKS))
            repository.bookmarkResult = Result.failure(IllegalStateException("네트워크 오류"))
            val viewModel = createViewModel(repository)
            runCurrent()

            viewModel.onIntent(BookmarkIntent.BookmarkClick("q1"))
            runCurrent()

            assertEquals(emptyMap(), viewModel.uiState.value.bookmarkChanges)
        }
    }

    private fun createViewModel(repository: FakeBookmarkRepository): BookmarkViewModel =
        BookmarkViewModel(
            getBookmarkedQuestions = GetBookmarkedQuestionsUseCase(repository),
            bookmarkQuestion = BookmarkQuestionUseCase(repository),
        )

    private companion object {
        private val BOOKMARKS =
            BookmarkedQuestions(
                totalCount = 1,
                availableProjects = listOf(AvailableProject(projectId = "p1", projectName = "flask")),
                bookmarks =
                    listOf(
                        DomainBookmarkedQuestion(
                            projectId = "p1",
                            projectName = "flask",
                            setId = "s1",
                            setLabel = "Set 1",
                            problemNumber = 3,
                            questionId = "q1",
                            question = "블루프린트의 목적은?",
                        ),
                    ),
            )
    }
}
