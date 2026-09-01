import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.nexters.hytime.gitit.buildlogic"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvmToolchain.get().toInt())
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.jvmToolchain.get())
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.composeCompiler.gradlePlugin)
    implementation(libs.ktlint.gradlePlugin)
    implementation(libs.google.services.gradlePlugin)
    implementation(libs.kotzilla.gradlePlugin)

    // 생성된 `LibrariesForLibs` 타입을 노출한다.
    // 이게 있어야 precompiled script plugin에서 `the<LibrariesForLibs>()`로
    // 루트 버전 카탈로그(libs)를 읽을 수 있다.
    // 리플렉션 체인의 nullable(Class.superclass, ProtectionDomain.codeSource)을 명시적으로 처리한다.
    val catalogClasspath =
        libs.javaClass.superclass?.protectionDomain?.codeSource?.location
            ?: error("생성된 버전 카탈로그 접근자 JAR을 찾을 수 없습니다")
    implementation(files(catalogClasspath))
}
