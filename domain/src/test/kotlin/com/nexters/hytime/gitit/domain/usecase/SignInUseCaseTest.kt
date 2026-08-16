package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/** [SignInUseCase]가 로그인 성공 세션을 저장하는지 검증한다. */
class SignInUseCaseTest {
    /** 백엔드 로그인이 성공하면 반환하기 전에 같은 세션을 저장하는지 검증한다. */
    @Test
    fun invoke_loginSucceedsSavesSession() {
        val session = LoginSession("access-token", "refresh-token", true)
        val storage = FakeLoginSessionStorage()
        val useCase =
            SignInUseCase(
                tokenProvider =
                    object : AuthTokenProvider {
                        override suspend fun obtainToken(): String = "google-token"
                    },
                accountRepository = FakeAccountRepository(session),
                sessionStorage = storage,
            )

        val (result, storedSession) =
            runBlocking {
                useCase() to storage.load()
            }

        assertEquals(Unit, result.getOrThrow())
        assertEquals(session, storedSession)
    }
}

/** 로그인만 성공으로 응답하고 나머지 회원 API는 호출되지 않는지 확인한다. */
private class FakeAccountRepository(
    private val session: LoginSession,
) : AccountRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<LoginSession> = Result.success(session)

    override suspend fun registerDevice(deviceInfo: DeviceInfo): Result<Unit> = Result.success(Unit)

    override suspend fun getMemberProfile(): Result<MemberProfile> = error("호출되면 안 됩니다.")

    override suspend fun curateMember(curation: MemberCuration): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun updatePosition(position: Position): Result<Unit> = error("호출되면 안 됩니다.")
}

/** 테스트 중 메모리에만 세션을 보관한다. */
private class FakeLoginSessionStorage : LoginSessionStorage {
    private var session: LoginSession? = null

    override suspend fun save(session: LoginSession) {
        this.session = session
    }

    override suspend fun load(): LoginSession? = session

    override suspend fun clear() {
        session = null
    }
}
