package com.nexters.hytime.gitit.feature.onboarding

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.repository.AuthRepository
import com.nexters.hytime.gitit.domain.repository.MemberRepository
import com.nexters.hytime.gitit.domain.usecase.SignInUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** [OnboardingViewModel]의 로그인 분기와 큐레이션 제출 상태를 검증한다. */
@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
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

    /** 큐레이션이 필요 없는 회원은 로그인 직후 홈으로 이동하는지 검증한다. */
    @Test
    fun confirmTerms_existingMember_navigatesHome() =
        runTest(dispatcher) {
            val viewModel = viewModel(needsCuration = false)
            agreeToTerms(viewModel)
            val event = async { viewModel.events.first() }

            viewModel.onIntent(OnboardingIntent.ConfirmTerms)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(OnboardingEvent.NavigateToHome, event.await())
            assertNull(viewModel.uiState.value.curation)
        }

    /** 큐레이션이 필요한 회원은 로그인 뒤 개발 분야 선택 단계에 진입하는지 검증한다. */
    @Test
    fun confirmTerms_newMember_showsPositionStep() =
        runTest(dispatcher) {
            val viewModel = viewModel(needsCuration = true)
            agreeToTerms(viewModel)

            viewModel.onIntent(OnboardingIntent.ConfirmTerms)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                CurationStep.Position,
                viewModel.uiState.value.curation
                    ?.step,
            )
        }

    /** 마지막 단계 제출 성공 시 입력값을 저장하고 중간 스플래시로 이동하는지 검증한다. */
    @Test
    fun curationNext_validInput_savesCurationAndNavigatesToSplash() =
        runTest(dispatcher) {
            val memberRepository = FakeMemberRepository()
            val viewModel = viewModel(needsCuration = true, memberRepository = memberRepository)
            enterCuration(viewModel)
            val event = async { viewModel.events.first() }

            viewModel.onIntent(OnboardingIntent.CurationNext)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                MemberCuration(Position.ANDROID, CareerLevel.JUNIOR),
                memberRepository.receivedCuration,
            )
            assertEquals(OnboardingEvent.NavigateToIntermediateSplash, event.await())
        }

    /** 큐레이션 등록 실패 시 마지막 단계에 머물고 다시 제출할 수 있는지 검증한다. */
    @Test
    fun curationNext_saveFails_showsRetryableError() =
        runTest(dispatcher) {
            val memberRepository = FakeMemberRepository(Result.failure(IllegalStateException("network")))
            val viewModel = viewModel(needsCuration = true, memberRepository = memberRepository)
            enterCuration(viewModel)

            viewModel.onIntent(OnboardingIntent.CurationNext)
            dispatcher.scheduler.advanceUntilIdle()

            val curation = assertNotNull(viewModel.uiState.value.curation)
            assertEquals(CurationStep.CareerLevel, curation.step)
            assertTrue(curation.hasError)
            assertFalse(curation.isSubmitting)
        }

    /**
     * 큐레이션 마지막 단계까지 유효한 값을 입력한다.
     *
     * @param viewModel 입력을 전달할 대상
     */
    private fun enterCuration(viewModel: OnboardingViewModel) {
        agreeToTerms(viewModel)
        viewModel.onIntent(OnboardingIntent.ConfirmTerms)
        dispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(OnboardingIntent.CurationPositionSelected(Position.ANDROID))
        viewModel.onIntent(OnboardingIntent.CurationNext)
        viewModel.onIntent(OnboardingIntent.CurationCareerLevelSelected(CareerLevel.JUNIOR))
    }

    /**
     * 테스트 ViewModel의 필수 약관을 모두 선택한다.
     *
     * @param viewModel 약관 상태를 변경할 대상
     */
    private fun agreeToTerms(viewModel: OnboardingViewModel) {
        viewModel.onIntent(OnboardingIntent.ToggleAllTerms)
    }

    /**
     * 로그인 결과와 회원 저장소를 지정해 ViewModel을 만든다.
     *
     * @param needsCuration 로그인 뒤 큐레이션 필요 여부
     * @param memberRepository 큐레이션 요청을 받을 테스트 저장소
     * @return 테스트 의존성이 주입된 ViewModel
     */
    private fun viewModel(
        needsCuration: Boolean,
        memberRepository: FakeMemberRepository = FakeMemberRepository(),
    ): OnboardingViewModel {
        val session = LoginSession("access", "refresh", needsCuration)
        return OnboardingViewModel(
            signInUseCase =
                SignInUseCase(
                    tokenProvider =
                        object : AuthTokenProvider {
                            override suspend fun obtainToken(): String = "google-token"
                        },
                    authRepository =
                        object : AuthRepository {
                            override suspend fun signInWithGoogle(idToken: String): Result<LoginSession> = Result.success(session)
                        },
                    sessionStorage = FakeLoginSessionStorage(),
                ),
            memberRepository = memberRepository,
        )
    }
}

/**
 * 큐레이션 호출을 기록하는 테스트용 회원 저장소다.
 *
 * @property result 큐레이션 저장 요청이 반환할 결과
 */
private class FakeMemberRepository(
    private val result: Result<Unit> = Result.success(Unit),
) : MemberRepository {
    /** 마지막으로 전달받은 큐레이션 정보다. */
    var receivedCuration: MemberCuration? = null

    override suspend fun curateMember(curation: MemberCuration): Result<Unit> {
        receivedCuration = curation
        return result
    }

    override suspend fun getMemberProfile(): Result<MemberProfile> = error("호출되면 안 됩니다.")

    override suspend fun registerDevice(deviceInfo: DeviceInfo): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun updatePosition(position: Position): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun updateCareerLevel(careerLevel: CareerLevel): Result<Unit> = error("호출되면 안 됩니다.")
}

/** 로그인 세션을 메모리에 보관하는 테스트 저장소다. */
private class FakeLoginSessionStorage : LoginSessionStorage {
    /** 마지막으로 저장된 로그인 세션이다. */
    private var session: LoginSession? = null

    override suspend fun save(session: LoginSession) {
        this.session = session
    }

    override suspend fun load(): LoginSession? = session

    override suspend fun clear() {
        session = null
    }
}
