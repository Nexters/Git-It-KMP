import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("gitit.ktlint")
}

val libs = the<LibrariesForLibs>()

configureJvmTarget()

kotlin {
    jvm()

    android {
        // 모듈 이름에서 namespace를 유도한다. 예: `designsystem` -> com.nexters.hytime.gitit.designsystem
        // 이 때문에 모듈 이름에는 하이픈을 쓰지 않는다.
        namespace = "com.nexters.hytime.gitit.${project.name}"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        // com.android.kotlin.multiplatform.library는 기본으로 Android 리소스 처리를 켜지 않는다.
        // 켜지 않으면 Compose Multiplatform 리소스(drawable/font)가 그룹 경로(
        // git_it_kmp.<모듈>.generated.resources) 없이 루트로 묶여 런타임 MissingResourceException 발생.
        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
