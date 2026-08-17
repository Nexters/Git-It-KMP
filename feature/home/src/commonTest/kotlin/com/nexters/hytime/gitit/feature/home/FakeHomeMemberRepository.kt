package com.nexters.hytime.gitit.feature.home

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.repository.MemberRepository

/**
 * 프로필 조회에 지정한 결과만 돌려주는 테스트용 회원 리포지토리다.
 *
 * @property profileResult 프로필 조회에 돌려줄 결과
 */
internal class FakeHomeMemberRepository(
    private val profileResult: Result<MemberProfile>,
) : MemberRepository {
    override suspend fun getMemberProfile(): Result<MemberProfile> = profileResult

    override suspend fun registerDevice(deviceInfo: DeviceInfo): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun curateMember(curation: MemberCuration): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun updatePosition(position: Position): Result<Unit> = error("호출되면 안 됩니다.")

    override suspend fun updateCareerLevel(careerLevel: CareerLevel): Result<Unit> = error("호출되면 안 됩니다.")
}
