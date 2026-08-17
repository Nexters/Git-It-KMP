@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.presentation.splash

import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** [SplashViewModel]의 저장 세션 및 토큰 검증 분기를 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    /** ViewModel coroutine 실행을 제어할 테스트 dispatcher다. */
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

    /** 저장된 세션이 없으면 서버 호출 없이 온보딩으로 이동하는지 검증한다. */
    @Test
    fun init_저장된세션이없으면_온보딩으로이동한다() =
        runTest(dispatcher) {
            val authRepository = FakeSplashAuthRepository()
            val viewModel = SplashViewModel(authRepository, FakeSplashSessionStorage())

            advanceUntilIdle()

            assertEquals(SplashSideEffect.NavigateToOnboarding, viewModel.sideEffects.first())
            assertEquals(0, authRepository.verifyCallCount)
            assertEquals(false, viewModel.uiState.value.isCheckingToken)
        }

    /** 저장된 토큰이 유효하면 홈으로 이동하는지 검증한다. */
    @Test
    fun init_토큰검증에성공하면_홈으로이동한다() =
        runTest(dispatcher) {
            val authRepository = FakeSplashAuthRepository()
            val viewModel = SplashViewModel(authRepository, FakeSplashSessionStorage(SESSION))

            advanceUntilIdle()

            assertEquals(SplashSideEffect.NavigateToHome, viewModel.sideEffects.first())
            assertEquals(1, authRepository.verifyCallCount)
        }

    /** 저장된 토큰 검증에 실패하면 온보딩으로 이동하는지 검증한다. */
    @Test
    fun init_토큰검증에실패하면_온보딩으로이동한다() =
        runTest(dispatcher) {
            val authRepository = FakeSplashAuthRepository(Result.failure(IllegalStateException("unauthorized")))
            val viewModel = SplashViewModel(authRepository, FakeSplashSessionStorage(SESSION))

            advanceUntilIdle()

            assertEquals(SplashSideEffect.NavigateToOnboarding, viewModel.sideEffects.first())
            assertEquals(1, authRepository.verifyCallCount)
        }

    private companion object {
        /** 토큰 검증을 시도할 저장 세션이다. */
        val SESSION = LoginSession("access-token", "refresh-token", needsCuration = false)
    }
}

/**
 * 토큰 검증 결과와 호출 횟수를 제공하는 테스트 저장소다.
 *
 * @property verifyResult 토큰 검증이 반환할 결과
 */
private class FakeSplashAuthRepository(
    private val verifyResult: Result<Unit> = Result.success(Unit),
) : AuthRepository {
    /** 토큰 검증이 호출된 횟수다. */
    var verifyCallCount: Int = 0

    override suspend fun verifyAccessToken(): Result<Unit> {
        verifyCallCount += 1
        return verifyResult
    }

    override suspend fun signInWithGoogle(idToken: String): Result<LoginSession> = error("호출되면 안 됩니다.")
}

/**
 * 지정한 로그인 세션을 반환하는 테스트 저장소다.
 *
 * @property session 불러올 로그인 세션
 */
private class FakeSplashSessionStorage(
    private var session: LoginSession? = null,
) : LoginSessionStorage {
    override suspend fun save(session: LoginSession) {
        this.session = session
    }

    override suspend fun load(): LoginSession? = session

    override suspend fun clear() {
        session = null
    }
}
