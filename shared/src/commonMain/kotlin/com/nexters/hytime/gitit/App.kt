package com.nexters.hytime.gitit

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.navigation.AppNavHost

/**
 * 앱 공통 테마와 최상위 내비게이션을 제공한다.
 *
 * @param sharedRepositoryUrl Android 공유로 전달되어 아직 처리하지 않은 저장소 URL
 * @param onSharedRepositoryUrlConsumed 공유 URL을 이동하거나 폐기한 뒤 호출하는 콜백
 */
@Composable
fun App(
    sharedRepositoryUrl: String? = null,
    onSharedRepositoryUrlConsumed: () -> Unit = {},
) {
    GitItTheme {
        AppNavHost(
            sharedRepositoryUrl = sharedRepositoryUrl,
            onSharedRepositoryUrlConsumed = onSharedRepositoryUrlConsumed,
        )
    }
}

@Preview
@Composable
fun AppPreview() = App()
