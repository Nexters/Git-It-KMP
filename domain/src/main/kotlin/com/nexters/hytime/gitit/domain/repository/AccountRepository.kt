package com.nexters.hytime.gitit.domain.repository

import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position

/**
 * 로그인한 사용자의 인증과 회원 정보를 다루는 도메인 리포지토리 계약이다.
 *
 * 구현체는 `data` 모듈에 위치하며, ID Token을 백엔드로 전송해 검증된 세션을 얻고
 * 현재 회원의 프로필과 설정을 읽고 쓰는 책임을 진다. `domain`은 인증 수단(Google)이나
 * 네트워크 구조(Ktor, DTO)를 알지 않는다.
 *
 * 회원 정보 관련 함수는 현재 로그인 세션을 대상으로 하므로 회원 식별자를 받지 않는다.
 */
interface AccountRepository {
    /**
     * Google ID Token으로 로그인을 수행한다.
     *
     * @param idToken Google이 발급한 OIDC ID Token (JWT)
     * @return 백엔드 검증 결과를 담은 [Result]. 성공 시 로그인 세션, 실패 시 예외.
     */
    suspend fun signInWithGoogle(idToken: String): Result<LoginSession>

    /**
     * 현재 회원의 푸시 발송 대상 기기 정보를 등록하거나 갱신한다.
     *
     * @param deviceInfo 서버에 덮어쓸 현재 앱 설치 정보
     * @return 등록 결과. 성공 시 [Unit], 실패 시 예외를 담는다
     */
    suspend fun registerDevice(deviceInfo: DeviceInfo): Result<Unit>

    /**
     * 현재 회원의 프로필과 학습 현황을 조회한다.
     *
     * @return 조회 결과. 성공 시 프로필, 실패 시 예외를 담는다
     */
    suspend fun getMemberProfile(): Result<MemberProfile>

    /**
     * 온보딩에서 받은 큐레이션 정보를 저장한다. 다시 호출하면 이전 값을 덮어쓴다.
     *
     * @param curation 저장할 이름·개발 분야·개발 수준
     * @return 저장 결과. 성공 시 [Unit], 실패 시 예외를 담는다
     */
    suspend fun curateMember(curation: MemberCuration): Result<Unit>

    /**
     * 개발 분야만 변경한다. 개발 수준에는 영향을 주지 않는다.
     *
     * @param position 새로 선택한 개발 분야
     * @return 변경 결과. 성공 시 [Unit], 실패 시 예외를 담는다
     */
    suspend fun updatePosition(position: Position): Result<Unit>
}
