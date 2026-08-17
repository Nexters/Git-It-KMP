package com.nexters.hytime.gitit.domain.model

/**
 * 회원이 고른 문제 난이도다.
 *
 * 직급이 아니라 프로젝트를 얼마나 깊이 파고들지로만 나눈다.
 * [L1] 오리엔테이션급 · [L2] 동작 이해 · [L3] 설계 이해.
 *
 * 서버와 이름으로 주고받으므로 상수 이름은 백엔드 `QuizLevel`과 일치시킨다.
 */
enum class QuizLevel {
    L1,
    L2,
    L3,
}
