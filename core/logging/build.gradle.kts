plugins {
    alias(libs.plugins.gitit.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(libs.koin.core)
        }
    }
}
