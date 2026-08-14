package com.nexters.hytime.gitit.data.mapping

import com.nexters.hytime.gitit.data.dto.LoginResponse
import com.nexters.hytime.gitit.domain.model.LoginSession

/**
 * 로그인 응답을 도메인 세션으로 변환한다.
 *
 * @return 네트워크 표현을 제거한 로그인 세션
 */
internal fun LoginResponse.toDomain(): LoginSession =
    LoginSession(
        accessToken = accessToken,
        refreshToken = refreshToken,
        needsCuration = needsCuration,
    )
