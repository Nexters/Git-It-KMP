package com.nexters.hytime.gitit.network.api

/** Ktor 같은 HTTP 구현 세부 사항을 숨기는 네트워크 통신 경계다. */
interface NetworkClient {
    /**
     * 서버에 요청을 전송하고 응답을 반환한다.
     *
     * @param request 서버에 전송할 요청 정보
     * @return HTTP 구현에 독립적인 응답 정보
     * @throws NetworkException HTTP 통신 자체에 실패한 경우
     */
    suspend fun execute(request: NetworkRequest): NetworkResponse
}
