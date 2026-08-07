package com.nexters.hytime.gitit

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.navigation.AppNavHost

/**
 * 앱 공통 테마와 최상위 내비게이션을 제공한다.
 */
@Composable
fun App() {
    GitItTheme {
        AppNavHost()
    }
}

@Preview
@Composable
fun AppPreview() = App()
