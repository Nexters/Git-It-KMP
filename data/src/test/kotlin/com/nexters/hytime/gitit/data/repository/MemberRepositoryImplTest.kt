@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.MemberCuration
import com.nexters.hytime.gitit.domain.model.Position
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** [MemberRepositoryImpl]의 회원 API 계약과 응답 매핑을 검증한다. */
class MemberRepositoryImplTest {
    /** 기기 정보 등록 API에 인증된 요청과 OpenAPI 필드명을 사용하는지 검증한다. */
    @Test
    fun registerDevice_알림이활성화되면_fid를전송한다() {
        val networkClient = FakeNetworkClient()
        val deviceInfo =
            DeviceInfo(
                deviceId = "firebase-installation-id",
                deviceType = "android",
                appVersion = "1.0",
                osVersion = "16",
                deviceToken = "firebase-installation-id",
            )

        val result = runBlocking { MemberRepositoryImpl(networkClient).registerDevice(deviceInfo) }

        assertEquals(Unit, result.getOrThrow())
        assertEquals("/api/v1/members/me/device", networkClient.requestedPath)
        assertEquals(
            """{"deviceId":"firebase-installation-id","deviceType":"android","appVersion":"1.0","osVersion":"16","deviceToken":"firebase-installation-id"}""",
            networkClient.requestBody,
        )
        assertEquals(true, networkClient.requestedAuthenticated)
    }

    /** 알림이 비활성화되면 deviceToken을 생략하고 성공하지 않은 응답을 실패로 변환하는지 검증한다. */
    @Test
    fun registerDevice_알림이비활성화되고응답이실패면_토큰없이실패를반환한다() {
        val networkClient = FakeNetworkClient("""{"success":false,"message":"등록 실패"}""")
        val deviceInfo =
            DeviceInfo(
                deviceId = "firebase-installation-id",
                deviceType = "android",
                appVersion = "1.0",
                osVersion = "16",
                deviceToken = null,
            )

        val result = runBlocking { MemberRepositoryImpl(networkClient).registerDevice(deviceInfo) }

        assertTrue(result.isFailure)
        assertEquals(
            """{"deviceId":"firebase-installation-id","deviceType":"android","appVersion":"1.0","osVersion":"16"}""",
            networkClient.requestBody,
        )
    }

    /** 프로필 조회가 인증된 GET을 보내고 응답을 도메인 모델로 매핑하는지 검증한다. */
    @Test
    fun getMemberProfile_성공하면_인증된GET으로조회하고도메인모델로매핑한다() {
        val networkClient = FakeNetworkClient(PROFILE_RESPONSE)

        val profile = runBlocking { MemberRepositoryImpl(networkClient).getMemberProfile() }.getOrThrow()

        assertEquals("GET", networkClient.requestedMethod)
        assertEquals("/api/v1/members/me", networkClient.requestedPath)
        assertEquals(true, networkClient.requestedAuthenticated)
        assertEquals("김이박", profile.name)
        assertEquals("gitit@example.com", profile.email)
        assertEquals(Position.ANDROID, profile.position)
        assertEquals(CareerLevel.JUNIOR, profile.careerLevel)
        assertEquals(3, profile.thisWeekSolvedCount)
        assertEquals(12, profile.thisMonthSolvedCount)
        assertEquals(2, profile.streakDays)
        assertEquals(listOf("월", "화"), profile.weeklyChart.map { it.dayLabel })
        assertEquals(listOf(1, 2), profile.weeklyChart.map { it.count })
    }

    /** 큐레이션 전이라 값이 비어 있거나 모르는 열거형이 오면 null로 떨어뜨리는지 검증한다. */
    @Test
    fun getMemberProfile_모르는열거형이면_null로매핑한다() {
        val networkClient =
            FakeNetworkClient(
                """{"success":true,"data":{"name":null,"email":null,"position":"DEVOPS","careerLevel":null,""" +
                    """"thisWeekSolvedCount":0,"thisMonthSolvedCount":0,"streakDays":0,"weeklyChart":[]}}""",
            )

        val profile = runBlocking { MemberRepositoryImpl(networkClient).getMemberProfile() }.getOrThrow()

        assertNull(profile.name)
        assertNull(profile.position)
        assertNull(profile.careerLevel)
        assertTrue(profile.weeklyChart.isEmpty())
    }

    /** 실패 응답과 데이터 누락을 프로필 조회 실패로 처리하는지 검증한다. */
    @Test
    fun getMemberProfile_응답이올바르지않으면_실패를반환한다() {
        val invalidResponses =
            listOf(
                """{"success":false,"code":"MEMBER-001","message":"회원을 찾을 수 없습니다"}""",
                """{"success":true,"data":null}""",
            )

        invalidResponses.forEach { response ->
            val result = runBlocking { MemberRepositoryImpl(FakeNetworkClient(response)).getMemberProfile() }
            assertTrue(result.isFailure, response)
        }
    }

    /** 큐레이션 등록이 열거형 이름을 그대로 전송하는지 검증한다. */
    @Test
    fun curateMember_성공하면_열거형이름으로요청한다() {
        val networkClient = FakeNetworkClient()
        val curation = MemberCuration(position = Position.ANDROID, careerLevel = CareerLevel.JUNIOR)

        val result = runBlocking { MemberRepositoryImpl(networkClient).curateMember(curation) }

        assertEquals(Unit, result.getOrThrow())
        assertEquals("/api/v1/members/me/curation", networkClient.requestedPath)
        assertEquals("""{"position":"ANDROID","careerLevel":"JUNIOR"}""", networkClient.requestBody)
        assertEquals(true, networkClient.requestedAuthenticated)
    }

    /** 개발 분야 변경이 전용 경로로 요청하는지 검증한다. */
    @Test
    fun updatePosition_성공하면_개발분야경로로요청한다() {
        val networkClient = FakeNetworkClient()

        val result = runBlocking { MemberRepositoryImpl(networkClient).updatePosition(Position.BACKEND) }

        assertEquals(Unit, result.getOrThrow())
        assertEquals("/api/v1/members/me/position", networkClient.requestedPath)
        assertEquals("""{"position":"BACKEND"}""", networkClient.requestBody)
    }

    /** 개발 수준 변경이 전용 경로로 요청하는지 검증한다. */
    @Test
    fun updateCareerLevel_성공하면_개발수준경로로요청한다() {
        val networkClient = FakeNetworkClient()

        val result = runBlocking { MemberRepositoryImpl(networkClient).updateCareerLevel(CareerLevel.SENIOR) }

        assertEquals(Unit, result.getOrThrow())
        assertEquals("/api/v1/members/me/career-level", networkClient.requestedPath)
        assertEquals("""{"careerLevel":"SENIOR"}""", networkClient.requestBody)
    }

    /** 본문 없는 회원 API가 실패를 응답하면 실패로 변환하는지 검증한다. */
    @Test
    fun 회원설정변경_응답이실패면_실패를반환한다() {
        val failureResponse = """{"success":false,"code":"MEMBER-001","message":"회원을 찾을 수 없습니다"}"""

        val positionResult =
            runBlocking { MemberRepositoryImpl(FakeNetworkClient(failureResponse)).updatePosition(Position.IOS) }
        val careerLevelResult =
            runBlocking { MemberRepositoryImpl(FakeNetworkClient(failureResponse)).updateCareerLevel(CareerLevel.MIDDLE) }

        assertTrue(positionResult.isFailure)
        assertTrue(careerLevelResult.isFailure)
    }

    private companion object {
        private const val PROFILE_RESPONSE =
            """{"success":true,"data":{"name":"김이박","email":"gitit@example.com","position":"ANDROID","careerLevel":"JUNIOR",""" +
                """"thisWeekSolvedCount":3,"thisMonthSolvedCount":12,"streakDays":2,""" +
                """"weeklyChart":[{"dayLabel":"월","count":1},{"dayLabel":"화","count":2}]}}"""
    }
}
