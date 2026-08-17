package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.repository.MemberRepository

/**
 * 회원의 개발 수준만 변경한다. 개발 분야에는 영향을 주지 않는다.
 *
 * @property repository 회원 정보를 제공하는 도메인 계약
 */
class UpdateCareerLevelUseCase(
    private val repository: MemberRepository,
) {
    /**
     * 새로 선택한 개발 수준을 서버에 저장한다.
     *
     * @param careerLevel 새로 선택한 개발 수준
     * @return 변경 결과 또는 실패 원인
     */
    suspend operator fun invoke(careerLevel: CareerLevel): Result<Unit> = repository.updateCareerLevel(careerLevel)
}
