package com.nexters.hytime.gitit.domain.model

/**
 * 푸시 발송 대상으로 등록할 앱 설치 정보를 나타낸다.
 *
 * @property deviceId 앱 설치를 식별하는 Firebase Installation ID
 * @property deviceType 서버가 플랫폼을 구분할 때 사용하는 값
 * @property appVersion 설치된 앱 버전
 * @property osVersion 기기 운영체제 버전
 * @property deviceToken 푸시 발송 식별자. 알림이 비활성화되어 있으면 `null`
 */
data class DeviceInfo(
    val deviceId: String,
    val deviceType: String,
    val appVersion: String,
    val osVersion: String,
    val deviceToken: String?,
)
