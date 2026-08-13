package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.network.api.NetworkClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/** [AccountRepositoryImpl]의 Google 로그인 API 계약과 응답 매핑을 검증한다. */
class AccountRepositoryImplTest {
    /** 실제 명세의 경로와 필드명으로 요청하고 로그인 세션을 반환하는지 검증한다. */
    @Test
    fun signInWithGoogle_successUsesOpenApiContractAndMapsSession() {
        val networkClient = LoginFakeNetworkClient()

        val session = runBlocking { AccountRepositoryImpl(networkClient).signInWithGoogle("google-token") }.getOrThrow()

        assertEquals("/api/v1/auth/login/google", networkClient.requestedPath)
        assertEquals("""{"idToken":"google-token"}""", networkClient.requestBody)
        assertEquals("access-token", session.accessToken)
        assertEquals("refresh-token", session.refreshToken)
        assertEquals(true, session.needsCuration)
    }
}

/** 테스트 로그인 응답을 역직렬화하며 마지막 POST 요청을 기록한다. */
private class LoginFakeNetworkClient : NetworkClient {
    /** 마지막으로 요청한 API 경로다. */
    var requestedPath: String = ""

    /** 마지막으로 직렬화한 요청 본문이다. */
    var requestBody: String = ""

    override suspend fun <Res : Any> get(
        url: String,
        headers: Map<String, String>,
        responseSerializer: KSerializer<Res>,
    ): Res = error("호출되면 안 됩니다.")

    override suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedPath = path
        requestBody = Json.encodeToString(requestSerializer, body)
        return Json.decodeFromString(
            responseSerializer,
            """{"success":true,"data":{"accessToken":"access-token","refreshToken":"refresh-token","needsCuration":true}}""",
        )
    }
}
