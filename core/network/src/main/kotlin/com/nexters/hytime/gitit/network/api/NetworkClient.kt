package com.nexters.hytime.gitit.network.api

import kotlinx.serialization.KSerializer

/**
 * Ktor 같은 HTTP 구현 세부 사항을 숨기는 네트워크 통신 경계다.
 *
 * 요청 본문은 [KSerializer]를 통해 자동 직렬화되고, 응답 본문은 자동 역직렬화된다.
 * 호출자는 문자열 encode/decode를 다루지 않는다.
 *
 * 경로를 받는 [get]/[post]/[delete]는 baseUrl 뒤에 경로를 붙이므로 백엔드 API 호출에 쓰고,
 * 전체 URL을 받는 [getAbsolute]는 GitHub처럼 baseUrl 밖의 서비스를 호출할 때만 쓴다.
 */
interface NetworkClient {
    /**
     * baseUrl 뒤에 [path]를 붙여 GET 요청을 보내고 응답 본문을 역직렬화해 반환한다.
     *
     * @param path baseUrl 뒤에 붙는 요청 경로 (예: `"/api/v1/members/me"`)
     * @param queryParameters 쿼리 문자열로 붙일 값. 인코딩은 구현체가 처리하므로 호출자는 원본 값을 넘긴다
     * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부
     * @param responseSerializer 응답 본문의 역직렬화기
     * @return 역직렬화된 응답
     * @throws NetworkException HTTP 통신 실패 또는 2xx 외 응답
     */
    suspend fun <Res : Any> get(
        path: String,
        queryParameters: Map<String, String> = emptyMap(),
        authenticated: Boolean = true,
        responseSerializer: KSerializer<Res>,
    ): Res

    /**
     * 절대 URL로 GET 요청을 보내고 응답 본문을 역직렬화해 반환한다.
     *
     * @param url 요청할 전체 URL
     * @param headers 요청에 추가할 HTTP 헤더. Ktor 타입을 노출하지 않도록 문자열로 받는다
     * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부. baseUrl과 다른 origin이면 무시된다
     * @param responseSerializer 응답 본문의 역직렬화기
     * @return 역직렬화된 응답
     * @throws NetworkException HTTP 통신 실패 또는 2xx 외 응답
     */
    suspend fun <Res : Any> getAbsolute(
        url: String,
        headers: Map<String, String> = emptyMap(),
        authenticated: Boolean = true,
        responseSerializer: KSerializer<Res>,
    ): Res

    /**
     * 타입 안전한 POST 요청을 보내고 응답 본문을 역직렬화해 반환한다.
     * [path]는 baseUrl 뒤에 붙는 경로다 (예: `"/auth/google"`).
     *
     * @param path baseUrl 뒤에 붙는 요청 경로
     * @param body 요청 본문. [requestSerializer]로 직렬화된다.
     * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부
     * @param requestSerializer 요청 본문의 직렬화기
     * @param responseSerializer 응답 본문의 역직렬화기
     * @return 역직렬화된 응답
     * @throws NetworkException HTTP 통신 실패 또는 2xx 외 응답
     */
    suspend fun <Req : Any, Res : Any> post(
        path: String,
        body: Req,
        authenticated: Boolean = true,
        requestSerializer: KSerializer<Req>,
        responseSerializer: KSerializer<Res>,
    ): Res

    /**
     * baseUrl 뒤에 [path]를 붙여 DELETE 요청을 보내고 응답 본문을 역직렬화해 반환한다.
     *
     * 서버가 본문 없는 성공 응답을 주더라도 `success` 여부를 읽어야 하므로 응답을 역직렬화한다.
     *
     * @param path baseUrl 뒤에 붙는 요청 경로
     * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부
     * @param responseSerializer 응답 본문의 역직렬화기
     * @return 역직렬화된 응답
     * @throws NetworkException HTTP 통신 실패 또는 2xx 외 응답
     */
    suspend fun <Res : Any> delete(
        path: String,
        authenticated: Boolean = true,
        responseSerializer: KSerializer<Res>,
    ): Res
}

/**
 * [NetworkClient.get]의 reified 편의 확장이다.
 *
 * @param path baseUrl 뒤에 붙는 요청 경로
 * @param queryParameters 쿼리 문자열로 붙일 값
 * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부
 * @return 역직렬화된 응답
 */
suspend inline fun <reified Res : Any> NetworkClient.get(
    path: String,
    queryParameters: Map<String, String> = emptyMap(),
    authenticated: Boolean = true,
): Res =
    get(
        path = path,
        queryParameters = queryParameters,
        authenticated = authenticated,
        responseSerializer = kotlinx.serialization.serializer(),
    )

/**
 * [NetworkClient.getAbsolute]의 reified 편의 확장이다.
 *
 * @param url 요청할 전체 URL
 * @param headers 요청에 추가할 HTTP 헤더
 * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부
 * @return 역직렬화된 응답
 */
suspend inline fun <reified Res : Any> NetworkClient.getAbsolute(
    url: String,
    headers: Map<String, String> = emptyMap(),
    authenticated: Boolean = true,
): Res =
    getAbsolute(
        url = url,
        headers = headers,
        authenticated = authenticated,
        responseSerializer = kotlinx.serialization.serializer(),
    )

/**
 * [NetworkClient.post]의 reified 편의 확장이다. 직렬화기를 명시하지 않아도 된다.
 *
 * @param path baseUrl 뒤에 붙는 요청 경로
 * @param body 직렬화할 요청 본문
 * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부
 * @return 역직렬화된 응답
 */
suspend inline fun <reified Req : Any, reified Res : Any> NetworkClient.post(
    path: String,
    body: Req,
    authenticated: Boolean = true,
): Res =
    post(
        path = path,
        body = body,
        authenticated = authenticated,
        requestSerializer = kotlinx.serialization.serializer(),
        responseSerializer = kotlinx.serialization.serializer(),
    )

/**
 * [NetworkClient.delete]의 reified 편의 확장이다.
 *
 * @param path baseUrl 뒤에 붙는 요청 경로
 * @param authenticated 백엔드 액세스 토큰을 요청에 포함할지 여부
 * @return 역직렬화된 응답
 */
suspend inline fun <reified Res : Any> NetworkClient.delete(
    path: String,
    authenticated: Boolean = true,
): Res =
    delete(
        path = path,
        authenticated = authenticated,
        responseSerializer = kotlinx.serialization.serializer(),
    )
