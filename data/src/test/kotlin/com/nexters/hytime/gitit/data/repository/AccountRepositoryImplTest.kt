@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.network.api.NetworkClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [AccountRepositoryImpl]의 Google 로그인 API 계약과 응답 매핑을 검증한다. */
class AccountRepositoryImplTest {
    /** 실제 명세의 경로와 필드명으로 요청하고 로그인 세션을 반환하는지 검증한다. */
    @Test
    fun signInWithGoogle_successUsesOpenApiContractAndMapsSession() {
        val networkClient = LoginFakeNetworkClient(SUCCESS_RESPONSE)

        val session = runBlocking { AccountRepositoryImpl(networkClient).signInWithGoogle("google-token") }.getOrThrow()

        assertEquals("/api/v1/auth/login/google", networkClient.requestedPath)
        assertEquals("""{"idToken":"google-token"}""", networkClient.requestBody)
        assertEquals(false, networkClient.requestedAuthenticated)
        assertEquals("access-token", session.accessToken)
        assertEquals("refresh-token", session.refreshToken)
        assertEquals(true, session.needsCuration)
    }

    /** 실패 응답, 누락 데이터, 빈 토큰을 로그인 실패로 처리하는지 검증한다. */
    @Test
    fun signInWithGoogle_invalidResponseReturnsFailure() {
        val invalidResponses =
            listOf(
                """{"success":false,"data":{"accessToken":"access-token","refreshToken":"refresh-token","needsCuration":false}}""",
                """{"success":true,"data":null}""",
                """{"success":true,"data":{"accessToken":"","refreshToken":"refresh-token","needsCuration":false}}""",
                """{"success":true,"data":{"accessToken":"access-token","refreshToken":" ","needsCuration":false}}""",
            )

        invalidResponses.forEach { response ->
            val result = runBlocking { AccountRepositoryImpl(LoginFakeNetworkClient(response)).signInWithGoogle("token") }
            assertTrue(result.isFailure, response)
        }
    }

    /** 기기 정보 등록 API에 인증된 요청과 OpenAPI 필드명을 사용하는지 검증한다. */
    @Test
    fun registerDevice_알림이활성화되면_fid를전송한다() {
        val networkClient = LoginFakeNetworkClient("""{"success":true}""")
        val deviceInfo =
            DeviceInfo(
                deviceId = "firebase-installation-id",
                deviceType = "android",
                appVersion = "1.0",
                osVersion = "16",
                deviceToken = "firebase-installation-id",
            )

        val result = runBlocking { AccountRepositoryImpl(networkClient).registerDevice(deviceInfo) }

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
        val networkClient = LoginFakeNetworkClient("""{"success":false,"message":"등록 실패"}""")
        val deviceInfo =
            DeviceInfo(
                deviceId = "firebase-installation-id",
                deviceType = "android",
                appVersion = "1.0",
                osVersion = "16",
                deviceToken = null,
            )

        val result = runBlocking { AccountRepositoryImpl(networkClient).registerDevice(deviceInfo) }

        assertTrue(result.isFailure)
        assertEquals(
            """{"deviceId":"firebase-installation-id","deviceType":"android","appVersion":"1.0","osVersion":"16"}""",
            networkClient.requestBody,
        )
    }

    private companion object {
        private const val SUCCESS_RESPONSE =
            """{"success":true,"data":{"accessToken":"access-token","refreshToken":"refresh-token","needsCuration":true}}"""
    }
}

/** 테스트 로그인 응답을 역직렬화하며 마지막 POST 요청을 기록한다. */
private class LoginFakeNetworkClient(
    private val responseBody: String,
) : NetworkClient {
    /** 마지막으로 요청한 API 경로다. */
    var requestedPath: String = ""

    /** 마지막으로 직렬화한 요청 본문이다. */
    var requestBody: String = ""

    /** 마지막 요청에 액세스 토큰 인증이 설정됐는지 여부다. */
    var requestedAuthenticated: Boolean = true

    override suspend fun <Res : Any> get(
        path: String,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res = error("호출되면 안 됩니다.")

    override suspend fun <Res : Any> getAbsolute(
        url: String,
        headers: Map<String, String>,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res = error("호출되면 안 됩니다.")

    override suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        authenticated: Boolean,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedPath = path
        requestBody = Json.encodeToString(requestSerializer, body)
        requestedAuthenticated = authenticated
        return Json.decodeFromString(
            responseSerializer,
            responseBody,
        )
    }
}
