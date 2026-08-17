@file:Suppress("ktlint:standard:function-naming")

package com.nexters.hytime.gitit.data.repository

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [AuthRepositoryImpl]의 로그인 API 계약과 응답 매핑을 검증한다. */
class AuthRepositoryImplTest {
    /** 토큰 검증 API를 인증된 GET으로 호출하는지 검증한다. */
    @Test
    fun verifyAccessToken_성공하면_인증된요청을보낸다() {
        val networkClient = FakeNetworkClient()

        val result = runBlocking { AuthRepositoryImpl(networkClient).verifyAccessToken() }

        assertEquals(Unit, result.getOrThrow())
        assertEquals("GET", networkClient.requestedMethod)
        assertEquals("/api/v1/auth/token", networkClient.requestedPath)
        assertEquals(true, networkClient.requestedAuthenticated)
    }

    /** 2xx 응답의 성공 값이 거짓이면 토큰 검증 실패로 처리하는지 검증한다. */
    @Test
    fun verifyAccessToken_응답이실패이면_실패를반환한다() {
        val result =
            runBlocking {
                AuthRepositoryImpl(FakeNetworkClient("""{"success":false,"message":"인증이 필요합니다"}"""))
                    .verifyAccessToken()
            }

        assertTrue(result.isFailure)
    }

    /** 실제 명세의 경로와 필드명으로 요청하고 로그인 세션을 반환하는지 검증한다. */
    @Test
    fun signInWithGoogle_성공하면_명세대로요청하고세션을매핑한다() {
        val networkClient = FakeNetworkClient(SUCCESS_RESPONSE)

        val session = runBlocking { AuthRepositoryImpl(networkClient).signInWithGoogle("google-token") }.getOrThrow()

        assertEquals("POST", networkClient.requestedMethod)
        assertEquals("/api/v1/auth/login/google", networkClient.requestedPath)
        assertEquals("""{"idToken":"google-token"}""", networkClient.requestBody)
        assertEquals(false, networkClient.requestedAuthenticated)
        assertEquals("access-token", session.accessToken)
        assertEquals("refresh-token", session.refreshToken)
        assertEquals(true, session.needsCuration)
    }

    /** 실패 응답, 누락 데이터, 빈 토큰을 로그인 실패로 처리하는지 검증한다. */
    @Test
    fun signInWithGoogle_응답이올바르지않으면_실패를반환한다() {
        val invalidResponses =
            listOf(
                """{"success":false,"data":{"accessToken":"access-token","refreshToken":"refresh-token","needsCuration":false}}""",
                """{"success":true,"data":null}""",
                """{"success":true,"data":{"accessToken":"","refreshToken":"refresh-token","needsCuration":false}}""",
                """{"success":true,"data":{"accessToken":"access-token","refreshToken":" ","needsCuration":false}}""",
            )

        invalidResponses.forEach { response ->
            val result = runBlocking { AuthRepositoryImpl(FakeNetworkClient(response)).signInWithGoogle("token") }
            assertTrue(result.isFailure, response)
        }
    }

    private companion object {
        private const val SUCCESS_RESPONSE =
            """{"success":true,"data":{"accessToken":"access-token","refreshToken":"refresh-token","needsCuration":true}}"""
    }
}
