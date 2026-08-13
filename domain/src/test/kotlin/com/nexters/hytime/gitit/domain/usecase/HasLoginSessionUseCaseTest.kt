package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** [HasLoginSessionUseCase]의 저장 세션 판정을 검증한다. */
class HasLoginSessionUseCaseTest {
    /** 저장된 세션 유무를 로그인 상태로 반환하는지 검증한다. */
    @Test
    fun invoke_sessionPresenceReturnsLoginState() {
        val storage = SessionStorage()
        val useCase = HasLoginSessionUseCase(storage)

        assertFalse(useCase())
        storage.save(LoginSession("access-token", "refresh-token", false))
        assertTrue(useCase())
    }
}

/** 테스트 중 메모리에 로그인 세션을 보관한다. */
private class SessionStorage : LoginSessionStorage {
    private var session: LoginSession? = null

    override fun save(session: LoginSession) {
        this.session = session
    }

    override fun load(): LoginSession? = session

    override fun clear() {
        session = null
    }
}
