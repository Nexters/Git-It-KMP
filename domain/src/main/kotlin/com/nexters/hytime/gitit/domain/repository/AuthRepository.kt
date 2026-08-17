package com.nexters.hytime.gitit.domain.repository

import com.nexters.hytime.gitit.domain.model.LoginSession

/**
 * 백엔드 Auth API에 대응하는 도메인 리포지토리 계약이다.
 *
 * 구현체는 `data` 모듈에 위치하며, 로그인 세션 발급과 액세스 토큰 검증을 담당한다.
 * `domain`은 인증 수단(Google)이나 네트워크 구조(Ktor, DTO)를 알지 않는다.
 */
interface AuthRepository {
    /**
     * 저장된 액세스 토큰이 백엔드에서 유효한지 확인한다.
     *
     * @return 유효성 확인 결과. 성공 시 [Unit], 실패 시 예외
     */
    suspend fun verifyAccessToken(): Result<Unit>

    /**
     * Google ID Token으로 로그인을 수행한다.
     *
     * @param idToken Google이 발급한 OIDC ID Token (JWT)
     * @return 백엔드 검증 결과를 담은 [Result]. 성공 시 로그인 세션, 실패 시 예외.
     */
    suspend fun signInWithGoogle(idToken: String): Result<LoginSession>
}
