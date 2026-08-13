package com.nexters.hytime.gitit.auth

import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession

/** 앱 프로세스가 살아 있는 동안 로그인 세션을 메모리에 보관한다. */
class InMemoryLoginSessionStorage : LoginSessionStorage {
    private var session: LoginSession? = null

    override fun save(session: LoginSession) {
        this.session = session
    }

    override fun load(): LoginSession? = session

    override fun clear() {
        session = null
    }
}
