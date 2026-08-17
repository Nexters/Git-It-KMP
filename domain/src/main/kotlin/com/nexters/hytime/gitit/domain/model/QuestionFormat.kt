package com.nexters.hytime.gitit.domain.model

/**
 * 문제 형식이다. 어느 제출 API를 부를지와 선택지가 채워져 있는지를 결정한다.
 *
 * 서버와 이름으로 주고받으므로 상수 이름은 백엔드 `QuestionFormat`과 일치시킨다.
 */
enum class QuestionFormat {
    MULTIPLE_CHOICE,
    ESSAY,
}
