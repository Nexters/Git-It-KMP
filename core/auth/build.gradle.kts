plugins {
    alias(libs.plugins.gitit.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.playServicesAuth)
            implementation(libs.google.identity.googleid)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
