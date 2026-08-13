package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
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
                accountRepository =
                    object : AccountRepository {
                        override suspend fun signInWithGoogle(idToken: String): Result<LoginSession> = Result.success(session)
                    },
                sessionStorage = storage,
            )

        val result = runBlocking { useCase() }

        assertEquals(Unit, result.getOrThrow())
        assertEquals(session, storage.load())
    }
}

/** 테스트 중 메모리에만 세션을 보관한다. */
private class FakeLoginSessionStorage : LoginSessionStorage {
    private var session: LoginSession? = null

    override fun save(session: LoginSession) {
        this.session = session
    }

    override fun load(): LoginSession? = session

    override fun clear() {
        session = null
    }
}
