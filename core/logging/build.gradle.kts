plugins {
    alias(libs.plugins.gitit.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kermit)
        }
    }
}
