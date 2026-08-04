package com.nexters.hytime.gitit.network.api

/**
 * 네트워크 통신 과정에서 발생한 오류를 표현한다.
 *
 * @param message 오류의 원인을 설명하는 메시지
 * @param cause 내부 HTTP 구현에서 발생한 원본 예외
 */
class NetworkException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
