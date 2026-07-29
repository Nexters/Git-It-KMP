package com.nexters.hytime.gitit.domain.auth

/**
 * 인증 토큰을 획득하는 도메인 포트다.
 *
 * 구체적인 인증 수단(Google, Apple 등)을 모르며, ID Token 문자열만 반환한다.
 * 구현체는 외부 모듈(`core:auth` 등)에서 제공하고, DI로 주입된다.
 */
interface AuthTokenProvider {
    /**
     * 인증 토큰(ID Token)을 획득한다.
     *
     * @return 백엔드 검증에 사용할 ID Token
     * @throws Exception 인증 실패, 사용자 취소 등
     */
    suspend fun obtainToken(): String
}
