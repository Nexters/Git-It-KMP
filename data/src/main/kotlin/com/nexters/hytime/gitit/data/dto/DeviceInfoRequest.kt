package com.nexters.hytime.gitit.data.dto

import kotlinx.serialization.Serializable

/**
 * 현재 회원에게 등록할 푸시 발송 대상 기기 정보다.
 *
 * @property deviceId 앱 설치를 식별하는 Firebase Installation ID
 * @property deviceType 서버가 플랫폼을 구분할 때 사용하는 값
 * @property appVersion 설치된 앱 버전
 * @property osVersion 기기 운영체제 버전
 * @property deviceToken 푸시 발송 식별자. 알림이 비활성화되면 직렬화에서 생략된다
 */
@Serializable
internal data class DeviceInfoRequest(
    val deviceId: String,
    val deviceType: String,
    val appVersion: String,
    val osVersion: String,
    val deviceToken: String? = null,
)
