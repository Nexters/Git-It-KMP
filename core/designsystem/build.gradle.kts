plugins {
    alias(libs.plugins.gitit.kmp.library.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // compose runtime/foundation/ui는 gitit.kmp.library.compose가 이미 넣어준다.
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
        }
    }
}
