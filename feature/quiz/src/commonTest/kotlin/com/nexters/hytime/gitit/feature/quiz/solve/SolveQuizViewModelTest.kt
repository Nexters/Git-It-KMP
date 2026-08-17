@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.quiz.solve

import com.nexters.hytime.gitit.designsystem.quiz.GitItMultipleChoiceAnswerState
import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.LearningSetSummary
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.Question
import com.nexters.hytime.gitit.domain.model.QuestionFormat
import com.nexters.hytime.gitit.domain.model.QuestionSource
import com.nexters.hytime.gitit.domain.usecase.GetLearningSetUseCase
import com.nexters.hytime.gitit.domain.usecase.GetProjectDetailUseCase
import com.nexters.hytime.gitit.domain.usecase.SubmitChoiceAnswerUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 문제 풀이 ViewModel의 세트 조회와 문제 순회를 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class SolveQuizViewModelTest {
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

    /** 세트를 조회하면 소개 정보와 문제 목록이 형식 순서대로 채워진다. */
    @Test
    fun init_세트조회에성공하면_문제목록을순서대로채운다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals("Set 1", state.setInfo.label)
            assertEquals("라우팅 흐름 따라가기", state.setInfo.title)
            assertEquals(3, state.questions.size)
            assertTrue(state.questions[0] is SolveQuizQuestionItem.MultipleChoice)
            assertTrue(state.questions[1] is SolveQuizQuestionItem.Essay)
            assertTrue(state.questions[2] is SolveQuizQuestionItem.MultipleChoice)
            assertEquals(listOf(1, 2, 3), state.questions.map(SolveQuizQuestionItem::number))
            assertEquals(QuizStep.Intro, state.step)
        }
    }

    /** 세트 식별자를 지정하지 않으면 다 풀지 않은 첫 세트를 고른다. */
    @Test
    fun init_세트식별자가없으면_덜푼첫세트를고른다() {
        runTest(dispatcher) {
            val repository = FakeSolveQuizRepository(Result.success(DETAIL), Result.success(LEARNING_SET))
            createViewModel(repository = repository, setId = null)
            runCurrent()

            assertEquals("s2", repository.requestedSetId)
        }
    }

    /** 시작하면 첫 문제 형식에 맞는 단계로 이동한다. */
    @Test
    fun start_첫문제가객관식이면_객관식단계로이동한다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()

            viewModel.onIntent(SolveQuizIntent.Start)

            val state = viewModel.uiState.value
            assertEquals(QuizStep.MultipleChoice, state.step)
            assertEquals("객관식 첫 문제", state.multipleChoiceQuestion.text)
            assertEquals(listOf("0", "1"), state.multipleChoiceQuestion.answers.map(QuizAnswer::id))
            assertEquals(listOf("A", "B"), state.multipleChoiceQuestion.answers.map(QuizAnswer::label))
        }
    }

    /** 다음으로 이동하면 문제별 선택·제출 상태가 초기화되고 형식에 맞는 단계로 바뀐다. */
    @Test
    fun next_다음문제로이동하면_문제별상태를초기화한다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Start)
            viewModel.onIntent(SolveQuizIntent.AnswerClick("0"))
            viewModel.onIntent(SolveQuizIntent.Submit)
            runCurrent()

            viewModel.onIntent(SolveQuizIntent.Next)

            val state = viewModel.uiState.value
            assertEquals(1, state.currentIndex)
            assertEquals(QuizStep.Essay, state.step)
            assertEquals(null, state.selectedAnswerId)
            assertFalse(state.isMultipleChoiceSubmitted)
            assertEquals(emptySet(), state.expandedAnswerIds)
            assertEquals("서술형 문제", state.essayQuestion.text)
        }
    }

    /** 마지막 문제를 마치면 완료 단계로 이동한다. */
    @Test
    fun next_마지막문제를마치면_완료단계로이동한다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Start)
            viewModel.onIntent(SolveQuizIntent.AnswerClick("0"))
            viewModel.onIntent(SolveQuizIntent.Submit)
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Next)
            viewModel.onIntent(SolveQuizIntent.Submit)
            viewModel.onIntent(SolveQuizIntent.Next)
            viewModel.onIntent(SolveQuizIntent.AnswerClick("1"))
            viewModel.onIntent(SolveQuizIntent.Submit)
            runCurrent()

            viewModel.onIntent(SolveQuizIntent.Next)

            assertEquals(QuizStep.Completed, viewModel.uiState.value.step)
        }
    }

    /** 제출하지 않은 문제에서는 다음으로 이동하지 않는다. */
    @Test
    fun next_제출전이면_이동하지않는다() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Start)

            viewModel.onIntent(SolveQuizIntent.Next)

            assertEquals(0, viewModel.uiState.value.currentIndex)
            assertEquals(QuizStep.MultipleChoice, viewModel.uiState.value.step)
        }
    }

    /** 서술형 답안은 최대 글자 수까지만 저장한다. */
    @Test
    fun onIntent_essayAnswerOverLimit_truncatesToMaximumLength() {
        runTest(dispatcher) {
            val viewModel = essayViewModel()

            viewModel.onIntent(SolveQuizIntent.EssayAnswerChange("가".repeat(ESSAY_ANSWER_MAX_LENGTH + 1)))

            assertEquals(ESSAY_ANSWER_MAX_LENGTH, viewModel.uiState.value.essayAnswer.length)
        }
    }

    /** 문제 번호별 북마크 토글이 독립적으로 유지된다. */
    @Test
    fun onIntent_bookmarkEachQuestion_keepsBothQuestionNumbers() {
        runTest(dispatcher) {
            val viewModel = createViewModel()
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Start)
            viewModel.onIntent(SolveQuizIntent.BookmarkClick)
            viewModel.onIntent(SolveQuizIntent.AnswerClick("0"))
            viewModel.onIntent(SolveQuizIntent.Submit)
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Next)

            viewModel.onIntent(SolveQuizIntent.BookmarkClick)

            assertEquals(setOf(1, 2), viewModel.uiState.value.bookmarkedQuestionNumbers)
        }
    }

    /** 뒤로가기는 진행 상태를 소개 단계로 되돌린다. */
    @Test
    fun onIntent_backClick_resetsToIntro() {
        runTest(dispatcher) {
            val viewModel = essayViewModel()

            viewModel.onIntent(SolveQuizIntent.BackClick)

            val state = viewModel.uiState.value
            assertEquals(QuizStep.Intro, state.step)
            assertEquals(0, state.currentIndex)
            assertEquals("", state.essayAnswer)
            assertEquals(3, state.questions.size)
        }
    }

    /** 제출이 성공하면 서버 채점 결과로 정답과 해설을 채우고 두 답안을 펼친다. */
    @Test
    fun submit_채점에성공하면_정답과해설을채운다() {
        runTest(dispatcher) {
            val repository =
                FakeSolveQuizRepository(
                    Result.success(DETAIL),
                    Result.success(LEARNING_SET),
                    Result.success(ChoiceAnswerResult(questionId = "q1", correct = false, answerIndex = 0, explanation = "해설")),
                )
            val viewModel = createViewModel(repository = repository)
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Start)
            viewModel.onIntent(SolveQuizIntent.AnswerClick("1"))

            viewModel.onIntent(SolveQuizIntent.Submit)
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals("q1", repository.submittedChoiceQuestionId)
            assertEquals(1, repository.submittedChoiceIndex)
            assertTrue(state.isMultipleChoiceSubmitted)
            assertEquals("0", state.multipleChoiceQuestion.correctAnswerId)
            assertEquals("해설", state.multipleChoiceQuestion.explanation)
            assertEquals(setOf("1", "0"), state.expandedAnswerIds)
        }
    }

    /** 제출이 실패하면 미제출 상태를 유지해 다시 시도할 수 있다. */
    @Test
    fun submit_제출이실패하면_미제출상태를유지한다() {
        runTest(dispatcher) {
            val repository =
                FakeSolveQuizRepository(
                    Result.success(DETAIL),
                    Result.success(LEARNING_SET),
                    Result.failure(IllegalStateException("네트워크 오류")),
                )
            val viewModel = createViewModel(repository = repository)
            runCurrent()
            viewModel.onIntent(SolveQuizIntent.Start)
            viewModel.onIntent(SolveQuizIntent.AnswerClick("1"))

            viewModel.onIntent(SolveQuizIntent.Submit)
            runCurrent()

            val state = viewModel.uiState.value
            assertFalse(state.isMultipleChoiceSubmitted)
            assertEquals("1", state.selectedAnswerId)
        }
    }

    /** 오답 결과가 선택한 답안과 실제 정답에 서로 다른 디자인 상태를 적용한다. */
    @Test
    fun answerCardState_incorrectResult_mapsIncorrectAndCorrectStates() {
        val gradedQuestion =
            QuizQuestion(
                id = "q1",
                number = 1,
                text = "문제",
                answers = listOf(QuizAnswer("0", "A", "정답"), QuizAnswer("1", "B", "오답"), QuizAnswer("2", "C", "다른 답")),
                correctAnswerId = "0",
            )
        val state =
            SolveQuizUiState(
                questions = listOf(SolveQuizQuestionItem.MultipleChoice(gradedQuestion)),
                step = QuizStep.MultipleChoice,
                selectedAnswerId = "1",
                isMultipleChoiceSubmitted = true,
                expandedAnswerIds = setOf("1", "0"),
            )

        assertEquals(GitItMultipleChoiceAnswerState.Incorrect, state.answerCardState("1"))
        assertEquals(GitItMultipleChoiceAnswerState.Correct, state.answerCardState("0"))
        assertEquals(GitItMultipleChoiceAnswerState.Folded, state.answerCardState("2"))
    }

    /** 서술형 단계까지 이동한 ViewModel을 만든다. */
    private fun TestScope.essayViewModel(): SolveQuizViewModel {
        val viewModel = createViewModel()
        runCurrent()
        viewModel.onIntent(SolveQuizIntent.Start)
        viewModel.onIntent(SolveQuizIntent.AnswerClick("0"))
        viewModel.onIntent(SolveQuizIntent.Submit)
        runCurrent()
        viewModel.onIntent(SolveQuizIntent.Next)
        return viewModel
    }

    private fun createViewModel(
        repository: FakeSolveQuizRepository =
            FakeSolveQuizRepository(
                Result.success(DETAIL),
                Result.success(LEARNING_SET),
                Result.success(ChoiceAnswerResult(questionId = "q1", correct = true, answerIndex = 0, explanation = "해설")),
            ),
        setId: String? = "s2",
    ): SolveQuizViewModel =
        SolveQuizViewModel(
            args = SolveQuizArgs(projectId = "project-1", setId = setId),
            getProjectDetail = GetProjectDetailUseCase(repository),
            getLearningSet = GetLearningSetUseCase(repository),
            submitChoiceAnswer = SubmitChoiceAnswerUseCase(repository),
        )

    private companion object {
        private val DETAIL =
            ProjectDetail(
                projectId = "project-1",
                repositoryUrl = "https://github.com/facebook/react",
                repositoryName = "react",
                repositoryImageUrl = "https://example.com/a.png",
                starCount = 100,
                techStack = listOf("TypeScript"),
                overallProgressPercent = 50,
                nextProblemId = "q1",
                sets =
                    listOf(
                        LearningSetSummary(setId = "s1", label = "Set 0", title = "다 푼 세트", problemCount = 3, completedCount = 3),
                        LearningSetSummary(setId = "s2", label = "Set 1", title = "라우팅 흐름 따라가기", problemCount = 3, completedCount = 1),
                    ),
            )

        private val LEARNING_SET =
            LearningSet(
                setId = "s2",
                title = "라우팅 흐름 따라가기",
                description = "라우팅이 한곳에 모이는 구조를 확인하는 세트",
                orientation = "안내",
                level = ProjectQuizLevel.L2,
                questions =
                    listOf(
                        Question(
                            questionId = "q1",
                            format = QuestionFormat.MULTIPLE_CHOICE,
                            text = "객관식 첫 문제",
                            choices = listOf("정답", "오답"),
                            sources =
                                listOf(
                                    QuestionSource(
                                        file = "src/flask/sansio/blueprints.py",
                                        startLine = 1,
                                        endLine = 40,
                                        symbol = "Blueprint",
                                        summary = "블루프린트 설정",
                                        url = "https://github.com/pallets/flask/blob/abc/src/flask/sansio/blueprints.py",
                                    ),
                                ),
                            myAnswer = null,
                        ),
                        Question(
                            questionId = "q2",
                            format = QuestionFormat.ESSAY,
                            text = "서술형 문제",
                            choices = emptyList(),
                            sources = emptyList(),
                            myAnswer = null,
                        ),
                        Question(
                            questionId = "q3",
                            format = QuestionFormat.MULTIPLE_CHOICE,
                            text = "객관식 둘째 문제",
                            choices = listOf("하나", "둘"),
                            sources = emptyList(),
                            myAnswer = null,
                        ),
                    ),
            )
    }
}
