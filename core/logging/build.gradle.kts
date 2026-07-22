plugins {
    alias(libs.plugins.gitit.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kermit)
            api(libs.koin.core)
        }
    }
}
