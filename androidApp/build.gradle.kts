import java.util.Properties

plugins {
    alias(libs.plugins.gitit.android.application)
}

// local.properties에서 민감한 설정 값을 읽어온다.
// providers API를 사용해 설정 캐시와 호환된다.
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

dependencies {
    implementation(projects.shared)
    implementation(projects.core.logging)
    implementation(projects.core.auth)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(libs.koin.android)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.nexters.hytime.gitit"
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.nexters.hytime.gitit"
        versionCode = 1
        versionName = "1.0"

        // local.properties에서 주입되는 설정 값들
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${localProperty("google.webClientId")}\"")
        buildConfigField("String", "BACKEND_BASE_URL", "\"${localProperty("google.backendUrl")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}
