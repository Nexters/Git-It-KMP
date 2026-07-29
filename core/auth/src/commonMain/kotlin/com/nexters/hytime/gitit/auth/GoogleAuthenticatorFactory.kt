package com.nexters.hytime.gitit.auth

/**
 * 플랫폼별 [GoogleAuthenticator] 인스턴스를 생성하는 팩토리 계약이다.
 *
 * Android는 `Context`와 Web Client ID가 필요하고, Desktop은 Desktop용 Client ID만
 * 필요하므로, 플랫폼마다 생성자 시그니처가 다르다. 이 인터페이스를 통해 상위 계층이
 * 플랫폼 차이 없이 인증기를 주입받을 수 있다.
 */
interface GoogleAuthenticatorFactory {
    /**
     * 구성된 OAuth 클라이언트 ID로 [GoogleAuthenticator]를 생성한다.
     *
     * @return 플랫폼별 인증기 인스턴스
     */
    fun create(): GoogleAuthenticator
}
