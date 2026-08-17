package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.ApiResponse
import com.nexters.hytime.gitit.data.dto.CareerLevelRequest
import com.nexters.hytime.gitit.data.dto.CurationRequest
import com.nexters.hytime.gitit.data.dto.DeviceInfoRequest
import com.nexters.hytime.gitit.data.dto.EmptyApiResponse
import com.nexters.hytime.gitit.data.dto.MemberProfileResponse
import com.nexters.hytime.gitit.data.dto.PositionRequest
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import com.nexters.hytime.gitit.domain.repository.MemberRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.get
import com.nexters.hytime.gitit.network.api.post

/**
 * 백엔드 Member API를 호출하는 저장소 구현체다.
 *
 * @property networkClient HTTP 구현을 숨긴 네트워크 클라이언트
 */
class MemberRepositoryImpl(
    private val networkClient: NetworkClient,
) : MemberRepository {
    override suspend fun getMemberProfile(): Result<MemberProfile> =
        runCatchingResult {
            networkClient
                .get<ApiResponse<MemberProfileResponse>>(PATH_MEMBER_PROFILE)
                .requireData("회원 정보 조회 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun registerDevice(deviceInfo: DeviceInfo): Result<Unit> =
        runCatchingResult {
            require(
                deviceInfo.deviceId.isNotBlank() &&
                    deviceInfo.deviceType.isNotBlank() &&
                    deviceInfo.appVersion.isNotBlank() &&
                    deviceInfo.osVersion.isNotBlank(),
            ) { "기기 정보의 필수 값이 비어 있습니다." }
            networkClient
                .post<DeviceInfoRequest, EmptyApiResponse>(
                    PATH_REGISTER_DEVICE,
                    DeviceInfoRequest(
                        deviceId = deviceInfo.deviceId,
                        deviceType = deviceInfo.deviceType,
                        appVersion = deviceInfo.appVersion,
                        osVersion = deviceInfo.osVersion,
                        deviceToken = deviceInfo.deviceToken,
                    ),
                ).requireSuccess("기기 정보 등록 응답이 올바르지 않습니다.")
        }

    override suspend fun curateMember(curation: MemberCuration): Result<Unit> =
        runCatchingResult {
            networkClient
                .post<CurationRequest, EmptyApiResponse>(
                    PATH_CURATION,
                    CurationRequest(
                        position = curation.position.name,
                        careerLevel = curation.careerLevel.name,
                    ),
                ).requireSuccess("회원 큐레이션 등록 응답이 올바르지 않습니다.")
        }

    override suspend fun updatePosition(position: Position): Result<Unit> =
        runCatchingResult {
            networkClient
                .post<PositionRequest, EmptyApiResponse>(
                    PATH_POSITION,
                    PositionRequest(position.name),
                ).requireSuccess("개발 분야 변경 응답이 올바르지 않습니다.")
        }

    override suspend fun updateCareerLevel(careerLevel: CareerLevel): Result<Unit> =
        runCatchingResult {
            networkClient
                .post<CareerLevelRequest, EmptyApiResponse>(
                    PATH_CAREER_LEVEL,
                    CareerLevelRequest(careerLevel.name),
                ).requireSuccess("개발 수준 변경 응답이 올바르지 않습니다.")
        }

    private companion object {
        private const val PATH_MEMBER_PROFILE = "/api/v1/members/me"
        private const val PATH_REGISTER_DEVICE = "/api/v1/members/me/device"
        private const val PATH_CURATION = "/api/v1/members/me/curation"
        private const val PATH_POSITION = "/api/v1/members/me/position"
        private const val PATH_CAREER_LEVEL = "/api/v1/members/me/career-level"
    }
}
