@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.feature.my

import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.usecase.GetMemberProfileUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** 설정 화면의 로그아웃 흐름을 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
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

    /** 로그아웃을 누르면 저장된 세션을 지우고 온보딩으로 이동한다. */
    @Test
    fun logoutClick_세션을지우고_온보딩으로이동한다() {
        runTest(dispatcher) {
            val storage = FakeLoginSessionStorage()
            val viewModel = createViewModel(storage)
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onLogoutClick()
            runCurrent()

            assertTrue(storage.isCleared, "로그인 세션을 지우지 않았다")
            assertEquals(SettingsSideEffect.NavigateToOnboarding, sideEffect.await())
        }
    }

    /** 세션 삭제가 실패해도 온보딩으로 이동한다. */
    @Test
    fun logoutClick_세션삭제가실패해도_온보딩으로이동한다() {
        runTest(dispatcher) {
            val storage = FakeLoginSessionStorage(clearError = IllegalStateException("저장소 오류"))
            val viewModel = createViewModel(storage)
            val sideEffect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.sideEffects.first() }

            viewModel.onLogoutClick()
            runCurrent()

            assertEquals(SettingsSideEffect.NavigateToOnboarding, sideEffect.await())
        }
    }

    private fun createViewModel(storage: LoginSessionStorage): SettingsViewModel =
        SettingsViewModel(
            getMemberProfile = GetMemberProfileUseCase(FakeMemberRepository()),
            sessionStorage = storage,
        )
}

/**
 * 삭제 호출 여부만 기록하는 테스트용 로그인 세션 저장소다.
 *
 * @property clearError 삭제 시 던질 예외. null이면 정상 삭제한다
 */
private class FakeLoginSessionStorage(
    private val clearError: Throwable? = null,
) : LoginSessionStorage {
    /** [clear]가 호출됐는지 여부다. */
    var isCleared: Boolean = false
        private set

    override suspend fun save(session: LoginSession) = error("호출되면 안 됩니다.")

    override suspend fun load(): LoginSession? = error("호출되면 안 됩니다.")

    override suspend fun clear() {
        clearError?.let { throw it }
        isCleared = true
    }
}
