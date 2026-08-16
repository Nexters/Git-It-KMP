package com.nexters.hytime.gitit.domain.model

/**
 * 회원이 선택한 개발 수준이다. 질문 난이도를 정하는 기준으로 쓰인다.
 *
 * 서버와 이름으로 주고받으므로 상수 이름은 백엔드 `CareerLevel`과 일치시킨다.
 */
enum class CareerLevel {
    ENTRY,
    JUNIOR,
    MIDDLE,
    SENIOR,
}
