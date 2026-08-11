package com.nexters.hytime.gitit.network.api

import kotlinx.serialization.KSerializer

/**
 * Ktor 같은 HTTP 구현 세부 사항을 숨기는 네트워크 통신 경계다.
 *
 * 요청 본문은 [KSerializer]를 통해 자동 직렬화되고, 응답 본문은 자동 역직렬화된다.
 * 호출자는 문자열 encode/decode를 다루지 않는다.
 */
interface NetworkClient {
    /**
     * 절대 URL로 GET 요청을 보내고 응답 본문을 역직렬화해 반환한다.
     *
     * @param url 요청할 전체 URL
     * @param headers 요청에 추가할 HTTP 헤더. Ktor 타입을 노출하지 않도록 문자열로 받는다
     * @param responseSerializer 응답 본문의 역직렬화기
     * @return 역직렬화된 응답
     * @throws NetworkException HTTP 통신 실패 또는 2xx 외 응답
     */
    suspend fun <Res : Any> get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        responseSerializer: KSerializer<Res>,
    ): Res

    /**
     * 타입 안전한 POST 요청을 보내고 응답 본문을 역직렬화해 반환한다.
     * [path]는 baseUrl 뒤에 붙는 경로다 (예: `"/auth/google"`).
     *
     * @param path baseUrl 뒤에 붙는 요청 경로
     * @param body 요청 본문. [requestSerializer]로 직렬화된다.
     * @param requestSerializer 요청 본문의 직렬화기
     * @param responseSerializer 응답 본문의 역직렬화기
     * @return 역직렬화된 응답
     * @throws NetworkException HTTP 통신 실패 또는 2xx 외 응답
     */
    suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res
}

/**
 * [NetworkClient.get]의 reified 편의 확장이다.
 *
 * @param url 요청할 전체 URL
 * @param headers 요청에 추가할 HTTP 헤더
 * @return 역직렬화된 응답
 */
suspend inline fun <reified Res : Any> NetworkClient.get(
    url: String,
    headers: Map<String, String> = emptyMap(),
): Res = get(url = url, headers = headers, responseSerializer = kotlinx.serialization.serializer())

/**
 * [NetworkClient.post]의 reified 편의 확장이다. 직렬화기를 명시하지 않아도 된다.
 */
suspend inline fun <reified Req : Any, reified Res : Any> NetworkClient.post(
    path: String,
    body: Req,
): Res =
    post(
        path = path,
        body = body,
        requestSerializer = kotlinx.serialization.serializer(),
        responseSerializer = kotlinx.serialization.serializer(),
    )
