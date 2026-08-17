package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.repository.MemberRepository

/**
 * 현재 회원의 프로필과 학습 현황을 조회한다.
 *
 * @property repository 회원 정보를 제공하는 도메인 계약
 */
class GetMemberProfileUseCase(
    private val repository: MemberRepository,
) {
    /**
     * 마이 화면에 표시할 프로필을 가져온다.
     *
     * @return 조회된 프로필 또는 실패 원인
     */
    suspend operator fun invoke(): Result<MemberProfile> = repository.getMemberProfile()
}
