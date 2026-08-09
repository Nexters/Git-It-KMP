import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.gitit.jvm.compose)
}

/**
 * local.properties에서 민감한 설정 값을 읽는다.
 *
 * providers API를 사용해 설정 캐시와 호환된다.
 *
 * @param key 읽을 프로퍼티 키
 * @param default 값이 없을 때 사용할 기본값. 기본값은 빈 문자열이다.
 * @return 읽은 프로퍼티 값. 없으면 [default]를 공백 제거해 반환한다.
 */
fun localProperty(
    key: String,
    default: String = "",
): String =
    providers
        .fileContents(rootProject.layout.projectDirectory.file("local.properties"))
        .asText
        .map { text: String ->
            Properties().apply { text.reader().use { reader -> load(reader) } }.getProperty(key)
        }.orElse(default)
        .get()
        .trim()

val googleDesktopClientId = localProperty("google.desktopClientId")
val googleDesktopClientSecret = localProperty("google.desktopClientSecret")
val backendBaseUrl = localProperty("api.baseUrl")

compose.desktop {
    application {
        mainClass = "com.nexters.hytime.gitit.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.nexters.hytime.gitit"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "com.nexters.hytime.gitit"
            }
        }
    }
}

// 빌드 시점에 Kotlin 소스 파일을 생성하여 local.properties 값을 코드에 박는다.
// BuildConfig 방식과 같아서 run 태스크, IDE 실행, 배포 패키징 모두에서 동작한다.
val generatedDir = layout.buildDirectory.dir("generated/authconfig/main/kotlin")

val generateAuthConfig =
    tasks.register("generateAuthConfig") {
        val outputDir = generatedDir
        val clientId = googleDesktopClientId
        val secret = googleDesktopClientSecret
        val url = backendBaseUrl
        inputs.property("clientId", clientId)
        inputs.property("secret", secret)
        inputs.property("url", url)
        outputs.dir(outputDir)
        doLast {
            val targetFile = outputDir.get().asFile.resolve("com/nexters/hytime/gitit/AuthConfig.kt")
            targetFile.parentFile.mkdirs()
            targetFile.writeText(
                """
                 package com.nexters.hytime.gitit

                internal object AuthConfig {
                    const val GOOGLE_DESKTOP_CLIENT_ID = "$clientId"
                     const val GOOGLE_DESKTOP_CLIENT_SECRET = "$secret"
                     const val BACKEND_BASE_URL = "$url"
                }
                """.trimIndent(),
            )
        }
    }

sourceSets {
    main {
        kotlin.srcDir(generatedDir)
    }
}

tasks.named("compileKotlin") {
    dependsOn(generateAuthConfig)
}

// ktlint가 생성된 소스를 스캔하기 전에 generateAuthConfig가 실행되도록 보장한다.
tasks.matching { it.name.startsWith("runKtlint") }.configureEach {
    dependsOn(generateAuthConfig)
}

dependencies {
    implementation(projects.shared)
    implementation(projects.core.logging)
    implementation(projects.core.auth)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.core.network)
    implementation(libs.koin.core)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}
