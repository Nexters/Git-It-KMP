package com.nexters.hytime.gitit.domain.util

import kotlin.coroutines.cancellation.CancellationException

/**
 * [runCatching]과 같지만 [CancellationException]을 [Result]로 감싸지 않고 그대로 전파한다.
 *
 * 코루틴 취소가 Result.failure로 처리되면 취소가 무시되므로, suspend 블록에서는
 * 반드시 이 함수를 사용해야 한다.
 */
suspend inline fun <T> runCatchingResult(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (e: Exception) {
        Result.failure(e)
    }
