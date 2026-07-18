plugins {
    id("org.jetbrains.kotlin.jvm")
}

// Android 타겟이 없는 순수 Kotlin 모듈.
// Android SDK를 참조할 수 없어서 플랫폼 비의존성이 빌드로 강제된다.
configureJvmTarget()
