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
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
