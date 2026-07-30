package com.nexters.hytime.gitit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nexters.hytime.gitit.presentation.signin.SignInScreen

/**
 * 앱의 최상위 Composable이다.
 *
 * [MaterialTheme]으로 기본 테마를 적용하고 [SignInScreen]을 렌더한다.
 */
@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize().safeContentPadding(),
            color = MaterialTheme.colorScheme.background,
        ) {
            SignInScreen()
        }
    }
}
