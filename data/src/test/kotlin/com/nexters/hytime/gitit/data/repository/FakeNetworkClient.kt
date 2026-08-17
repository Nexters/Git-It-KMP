package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.network.api.NetworkClient
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * 마지막 요청을 기록하고 지정한 본문을 역직렬화해 돌려주는 테스트용 네트워크 클라이언트다.
 *
 * @property responseBody 어느 메서드로 요청하든 돌려줄 응답 본문
 */
internal class FakeNetworkClient(
    private val responseBody: String = """{"success":true}""",
) : NetworkClient {
    /** 마지막 요청의 HTTP 메서드다. 요청이 없었으면 빈 문자열이다. */
    var requestedMethod: String = ""

    /** 마지막으로 요청한 API 경로다. */
    var requestedPath: String = ""

    /** 마지막으로 요청한 전체 URL이다. [getAbsolute]에만 채워진다. */
    var requestedUrl: String = ""

    /** 마지막 요청에 붙인 쿼리 파라미터다. */
    var requestedQueryParameters: Map<String, String> = emptyMap()

    /** 마지막 요청에 추가한 HTTP 헤더다. */
    var requestedHeaders: Map<String, String> = emptyMap()

    /** 마지막으로 직렬화한 요청 본문이다. */
    var requestBody: String = ""

    /** 마지막 요청에 액세스 토큰 인증이 설정됐는지 여부다. */
    var requestedAuthenticated: Boolean = true

    override suspend fun <Res : Any> get(
        path: String,
        queryParameters: Map<String, String>,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedMethod = "GET"
        requestedPath = path
        requestedQueryParameters = queryParameters
        requestedAuthenticated = authenticated
        return decode(responseSerializer)
    }

    override suspend fun <Res : Any> getAbsolute(
        url: String,
        headers: Map<String, String>,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedMethod = "GET"
        requestedUrl = url
        requestedHeaders = headers
        requestedAuthenticated = authenticated
        return decode(responseSerializer)
    }

    override suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        authenticated: Boolean,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedMethod = "POST"
        requestedPath = path
        requestBody = json.encodeToString(requestSerializer, body)
        requestedAuthenticated = authenticated
        return decode(responseSerializer)
    }

    override suspend fun <Res : Any> delete(
        path: String,
        authenticated: Boolean,
        responseSerializer: KSerializer<Res>,
    ): Res {
        requestedMethod = "DELETE"
        requestedPath = path
        requestedAuthenticated = authenticated
        return decode(responseSerializer)
    }

    /**
     * 설정된 응답 본문을 요청이 기대하는 타입으로 역직렬화한다.
     *
     * @param serializer 응답 본문의 역직렬화기
     * @return 역직렬화된 응답
     */
    private fun <Res : Any> decode(serializer: KSerializer<Res>): Res = json.decodeFromString(serializer, responseBody)

    private companion object {
        /** 실제 클라이언트와 같은 관용 설정을 쓴다. */
        private val json = Json { ignoreUnknownKeys = true }
    }
}
