package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.ApiResponse
import com.nexters.hytime.gitit.data.dto.EmptyApiResponse
import com.nexters.hytime.gitit.network.api.NetworkException

/**
 * 성공 응답의 본문을 꺼낸다.
 *
 * 서버가 2xx로 응답해도 `success`가 `false`이거나 본문이 비어 있을 수 있어 여기서 한 번 더 거른다.
 *
 * @param fallbackMessage 서버가 사유를 내려주지 않았을 때 사용할 오류 메시지
 * @return 검증을 통과한 응답 본문
 * @throws NetworkException 실패 응답이거나 본문이 없는 경우
 */
internal fun <T : Any> ApiResponse<T>.requireData(fallbackMessage: String): T =
    data?.takeIf { success } ?: throw NetworkException(message ?: fallbackMessage)

/**
 * 본문 없는 성공 응답을 검증한다.
 *
 * @param fallbackMessage 서버가 사유를 내려주지 않았을 때 사용할 오류 메시지
 * @throws NetworkException 서버가 실패를 응답한 경우
 */
internal fun EmptyApiResponse.requireSuccess(fallbackMessage: String) {
    if (!success) {
        throw NetworkException(message ?: fallbackMessage)
    }
}
