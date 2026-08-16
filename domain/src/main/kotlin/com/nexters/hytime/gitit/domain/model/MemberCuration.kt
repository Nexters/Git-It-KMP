package com.nexters.hytime.gitit.domain.model

/**
 * 온보딩에서 받아 서버에 저장할 회원 큐레이션 정보다.
 *
 * 소셜 로그인은 이름을 내려주지 않으므로 이름도 여기서 함께 받는다.
 *
 * @property name 회원이 직접 입력한 이름
 * @property position 관심 있는 개발 분야
 * @property careerLevel 스스로 평가한 개발 수준
 */
data class MemberCuration(
    val name: String,
    val position: Position,
    val careerLevel: CareerLevel,
)
