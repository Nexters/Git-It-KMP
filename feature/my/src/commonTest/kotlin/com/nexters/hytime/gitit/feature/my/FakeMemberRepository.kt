package com.nexters.hytime.gitit.feature.my

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.repository.MemberRepository

/**
 * 지정한 결과만 돌려주는 테스트용 회원 리포지토리다.
 *
 * @property profileResult 프로필 조회에 돌려줄 결과
 * @property updateResult 분야·수준 변경에 돌려줄 결과
 */
internal class FakeMemberRepository(
    private val profileResult: Result<MemberProfile> = Result.failure(IllegalStateException("설정되지 않은 호출입니다.")),
    private val updateResult: Result<Unit> = Result.success(Unit),
) : MemberRepository {
    /** 마지막으로 변경 요청한 개발 분야다. */
    var updatedPosition: Position? = null

    /** 마지막으로 변경 요청한 개발 수준이다. */
    var updatedCareerLevel: CareerLevel? = null

    override suspend fun getMemberProfile(): Result<MemberProfile> = profileResult

    override suspend fun registerDevice(deviceInfo: DeviceInfo): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun curateMember(curation: MemberCuration): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun updatePosition(position: Position): Result<Unit> {
        updatedPosition = position
        return updateResult
    }

    override suspend fun updateCareerLevel(careerLevel: CareerLevel): Result<Unit> {
        updatedCareerLevel = careerLevel
        return updateResult
    }
}
