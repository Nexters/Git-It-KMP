package com.nexters.hytime.gitit.network.api

/** Git-It API 호출 결과를 표현한다. */
data class NetworkResponse(
    /** 서버가 반환한 HTTP 상태 코드다. */
    val statusCode: Int,
    /** 서버가 반환한 응답 헤더다. */
    val headers: Map<String, List<String>>,
    /** 서버가 반환한 원본 응답 본문이다. */
    val body: String,
)
