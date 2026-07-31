plugins {
    alias(libs.plugins.gitit.android.application)
}

dependencies {
    implementation(projects.shared)
    implementation(projects.core.logging)
    implementation(projects.core.network)
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
