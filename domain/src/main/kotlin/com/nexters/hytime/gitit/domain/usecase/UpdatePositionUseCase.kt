package com.nexters.hytime.gitit.domain.usecase

import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.repository.MemberRepository

/**
 * 회원의 개발 분야만 변경한다. 개발 수준에는 영향을 주지 않는다.
 *
 * @property repository 회원 정보를 제공하는 도메인 계약
 */
class UpdatePositionUseCase(
    private val repository: MemberRepository,
) {
    /**
     * 새로 선택한 개발 분야를 서버에 저장한다.
     *
     * @param position 새로 선택한 개발 분야
     * @return 변경 결과 또는 실패 원인
     */
    suspend operator fun invoke(position: Position): Result<Unit> = repository.updatePosition(position)
}
