package com.nexters.hytime.gitit

import android.content.Intent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** GitHub 저장소 공유 Intent의 허용 범위를 검증한다. */
class SharedRepositoryIntentTest {
    /** 저장소 루트 URL을 텍스트 공유하면 주변 공백을 제거해 반환하는지 검증한다. */
    @Test
    fun resolveSharedRepositoryUrl_repositoryRoot_returnsUrl() {
        val url = "https://github.com/Nexters/Git-It-KMP"

        val result =
            resolveSharedRepositoryUrl(
                action = Intent.ACTION_SEND,
                mimeType = "text/plain",
                sharedText = "  $url/  ",
            )

        assertEquals("$url/", result)
    }

    /** 지원하지 않는 Intent 정보나 저장소 하위 링크를 공유하면 무시하는지 검증한다. */
    @Test
    fun resolveSharedRepositoryUrl_unsupportedShare_returnsNull() {
        val invalidShares =
            listOf(
                Triple(Intent.ACTION_VIEW, "text/plain", "https://github.com/Nexters/Git-It-KMP"),
                Triple(Intent.ACTION_SEND, "text/html", "https://github.com/Nexters/Git-It-KMP"),
                Triple(Intent.ACTION_SEND, "text/plain", "https://example.com/Nexters/Git-It-KMP"),
                Triple(Intent.ACTION_SEND, "text/plain", "https://github.com/Nexters/Git-It-KMP/issues"),
                Triple(Intent.ACTION_SEND, "text/plain", "https://github.com/Nexters/Git-It-KMP/blob/main/README.md"),
            )

        invalidShares.forEach { (action, mimeType, text) ->
            assertNull(resolveSharedRepositoryUrl(action, mimeType, text), text)
        }
    }
}
