import java.util.Properties

plugins {
    alias(libs.plugins.gitit.android.application)
}

/**
 * local.properties에서 민감한 설정 값을 읽어온다.
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

dependencies {
    implementation(projects.shared)
    implementation(projects.feature.onboarding)
    implementation(projects.core.logging)
    implementation(projects.core.auth)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.core.network)
    implementation(libs.koin.android)

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
    testImplementation(libs.kotlin.test.junit)
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
        buildConfigField("String", "BACKEND_BASE_URL", "\"${localProperty("api.baseUrl")}\"")
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
