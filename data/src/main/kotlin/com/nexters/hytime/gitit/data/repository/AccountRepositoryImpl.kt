package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.CurationRequest
import com.nexters.hytime.gitit.data.dto.DeviceInfoRequest
import com.nexters.hytime.gitit.data.dto.EmptyApiResponse
import com.nexters.hytime.gitit.data.dto.LoginApiResponse
import com.nexters.hytime.gitit.data.dto.MemberProfileApiResponse
import com.nexters.hytime.gitit.data.dto.SignInWithGoogleRequest
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.NetworkException
import com.nexters.hytime.gitit.network.api.get
import com.nexters.hytime.gitit.network.api.post

/**
 * 계정 인증과 회원 정보 API를 호출하는 저장소 구현체다.
 *
 * @property networkClient HTTP 구현을 숨긴 네트워크 클라이언트
 */
class AccountRepositoryImpl(
    private val networkClient: NetworkClient,
) : AccountRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<LoginSession> =
        runCatchingResult {
            val response =
                networkClient.post<SignInWithGoogleRequest, LoginApiResponse>(
                    PATH_SIGN_IN_GOOGLE,
                    SignInWithGoogleRequest(idToken),
                    authenticated = false,
                )
            val data =
                response.data?.takeIf {
                    response.success && it.accessToken.isNotBlank() && it.refreshToken.isNotBlank()
                } ?: throw NetworkException(response.message ?: "로그인 응답이 올바르지 않습니다.")
            data.toDomain()
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

    override suspend fun getMemberProfile(): Result<MemberProfile> =
        runCatchingResult {
            val response = networkClient.get<MemberProfileApiResponse>(PATH_MEMBER_PROFILE)
            val data =
                response.data?.takeIf { response.success }
                    ?: throw NetworkException(response.message ?: "회원 정보 조회 응답이 올바르지 않습니다.")
            data.toDomain()
        }

    override suspend fun curateMember(curation: MemberCuration): Result<Unit> =
        runCatchingResult {
            require(curation.name.isNotBlank()) { "이름이 비어 있습니다." }
            networkClient
                .post<CurationRequest, EmptyApiResponse>(
                    PATH_CURATION,
                    CurationRequest(
                        name = curation.name,
                        position = curation.position.name,
                        careerLevel = curation.careerLevel.name,
                    ),
                ).requireSuccess("회원 큐레이션 등록 응답이 올바르지 않습니다.")
        }

    /**
     * 본문 없는 성공 응답을 검증한다.
     *
     * @param fallbackMessage 서버가 사유를 내려주지 않았을 때 사용할 오류 메시지
     * @throws NetworkException 서버가 실패를 응답한 경우
     */
    private fun EmptyApiResponse.requireSuccess(fallbackMessage: String) {
        if (!success) {
            throw NetworkException(message ?: fallbackMessage)
        }
    }

    private companion object {
        private const val PATH_SIGN_IN_GOOGLE = "/api/v1/auth/login/google"
        private const val PATH_MEMBER_PROFILE = "/api/v1/members/me"
        private const val PATH_REGISTER_DEVICE = "/api/v1/members/me/device"
        private const val PATH_CURATION = "/api/v1/members/me/curation"
    }
}
