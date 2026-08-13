package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage

/**
 * 저장된 로그인 세션이 있는지 확인한다.
 *
 * @property sessionStorage 로그인 세션 저장소
 */
class HasLoginSessionUseCase(
    private val sessionStorage: LoginSessionStorage,
) {
    /**
     * 로그인 세션 존재 여부를 반환한다.
     *
     * @return 저장된 세션이 있으면 `true`
     */
    operator fun invoke(): Boolean = sessionStorage.load() != null
}
