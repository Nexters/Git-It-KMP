package com.nexters.hytime.gitit.domain.repository

import com.nexters.hytime.gitit.domain.model.LoginSession

/**
 * 계정 인증과 관련된 도메인 리포지토리 계약이다.
 *
 * 구현체는 `data` 모듈에 위치하며, Google ID Token을 백엔드로 전송해
 * 검증된 세션을 얻는 책임을 진다. `domain`은 인증 수단(Google)이나
 * 네트워크 구조(Ktor, DTO)를 알지 않는다.
 */
interface AccountRepository {
    /**
     * Google ID Token으로 로그인을 수행한다.
     *
     * @param idToken Google이 발급한 OIDC ID Token (JWT)
     * @return 백엔드 검증 결과를 담은 [Result]. 성공 시 로그인 세션, 실패 시 예외.
     */
    suspend fun signInWithGoogle(idToken: String): Result<LoginSession>
}
