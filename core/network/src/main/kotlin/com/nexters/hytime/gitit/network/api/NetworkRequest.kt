package com.nexters.hytime.gitit.network.api

/** Git-It API 호출에 필요한 요청 정보를 표현한다. */
data class NetworkRequest(
    /** 요청을 전송할 전체 URL이다. */
    val url: String,
    /** 서버에 수행할 HTTP 동작이다. */
    val method: NetworkMethod = NetworkMethod.GET,
    /** 요청에 추가할 HTTP 헤더다. */
    val headers: Map<String, String> = emptyMap(),
    /** 요청 본문이다. 본문이 없으면 `null`이다. */
    val body: String? = null,
)

/** 네트워크 요청에 사용할 HTTP 메서드다. */
enum class NetworkMethod {
    /** 리소스를 조회한다. */
    GET,

    /** 리소스를 생성한다. */
    POST,

    /** 리소스를 전체 갱신한다. */
    PUT,

    /** 리소스를 일부 갱신한다. */
    PATCH,

    /** 리소스를 삭제한다. */
    DELETE,
}
