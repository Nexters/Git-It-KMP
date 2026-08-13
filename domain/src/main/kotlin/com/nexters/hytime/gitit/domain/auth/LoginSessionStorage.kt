package com.nexters.hytime.gitit.domain.auth

import com.nexters.hytime.gitit.domain.model.LoginSession

/** 로그인 세션을 플랫폼 저장소에 보관하는 포트다. */
interface LoginSessionStorage {
    /**
     * 로그인 세션을 저장한다.
     *
     * @param session 백엔드에서 발급받은 토큰과 온보딩 상태
     */
    fun save(session: LoginSession)

    /**
     * 저장된 로그인 세션을 반환한다.
     *
     * @return 저장된 세션. 로그인 이력이 없거나 읽을 수 없으면 `null`
     */
    fun load(): LoginSession?

    /** 저장된 로그인 세션을 삭제한다. */
    fun clear()
}
