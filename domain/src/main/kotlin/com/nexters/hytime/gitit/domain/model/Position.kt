package com.nexters.hytime.gitit.domain.model

/**
 * 회원이 선택한 개발 분야다.
 *
 * 서버와 이름으로 주고받으므로 상수 이름은 백엔드 `Position`과 일치시킨다.
 */
enum class Position {
    BACKEND,
    FRONTEND,
    IOS,
    ANDROID,
}
