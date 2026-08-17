package com.nexters.hytime.gitit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** 재생성 중 보존할 미처리 공유 저장소 URL의 상태 키다. */
private const val SHARED_REPOSITORY_URL_STATE = "sharedRepositoryUrl"

/** 공유 대상으로 허용하는 GitHub 저장소 루트 URL 형식이다. */
private val GITHUB_REPOSITORY_URL =
    Regex(
        pattern = "^https://github\\.com/([A-Za-z0-9](?:[A-Za-z0-9-]{0,38}))/([A-Za-z0-9._-]+)/?$",
        option = RegexOption.IGNORE_CASE,
    )

/**
 * Android 공유 Intent에서 지원하는 GitHub 저장소 URL을 반환한다.
 *
 * @param action 수신한 Intent action
 * @param mimeType 공유 데이터의 MIME type
 * @param sharedText 공유된 텍스트
 * @return 저장소 루트 URL이면 공백을 제거한 값, 지원하지 않는 공유이면 null
 */
internal fun resolveSharedRepositoryUrl(
    action: String?,
    mimeType: String?,
    sharedText: CharSequence?,
): String? {
    if (action != Intent.ACTION_SEND || mimeType != "text/plain") return null
    return sharedText?.toString()?.trim()?.takeIf(GITHUB_REPOSITORY_URL::matches)
}

/**
 * Android 앱의 시작 화면과 Compose 콘텐츠를 호스팅한다.
 */
class MainActivity : ComponentActivity() {
    /** 공통 내비게이션이 아직 소비하지 않은 공유 저장소 URL이다. */
    private var sharedRepositoryUrl by mutableStateOf<String?>(null)

    /**
     * Activity를 만들고 최초 실행 Intent 또는 저장된 미처리 URL을 Compose 콘텐츠에 전달한다.
     *
     * @param savedInstanceState 재생성 전 저장된 Activity 상태
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            handleSharedIntent(intent)
        } else {
            sharedRepositoryUrl = savedInstanceState.getString(SHARED_REPOSITORY_URL_STATE)
        }

        setContent {
            App(
                sharedRepositoryUrl = sharedRepositoryUrl,
                onSharedRepositoryUrlConsumed = { sharedRepositoryUrl = null },
            )
        }
    }

    /**
     * 재생성 전에 아직 처리하지 않은 공유 URL을 저장한다.
     *
     * @param outState 새 Activity에 전달할 상태 Bundle
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SHARED_REPOSITORY_URL_STATE, sharedRepositoryUrl)
        super.onSaveInstanceState(outState)
    }

    /**
     * 실행 중인 Activity에 새로 전달된 공유 Intent를 처리한다.
     *
     * @param intent 새로 수신한 공유 Intent
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSharedIntent(intent)
    }

    /**
     * 유효한 저장소 공유이면 내비게이션에서 소비할 URL로 보관한다.
     *
     * @param intent GitHub 앱 등 외부 앱에서 전달된 Intent
     */
    private fun handleSharedIntent(intent: Intent) {
        resolveSharedRepositoryUrl(
            action = intent.action,
            mimeType = intent.type,
            sharedText = intent.getCharSequenceExtra(Intent.EXTRA_TEXT),
        )?.let { sharedRepositoryUrl = it }
    }
}
