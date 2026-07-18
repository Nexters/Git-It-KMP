plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

// 데스크톱 산출물의 JVM 바이트코드 레벨을 카탈로그의 javaVersion으로 고정한다.
configureJvmTarget()
