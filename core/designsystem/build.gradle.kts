plugins {
    alias(libs.plugins.gitit.kmp.library.compose)
}

compose.resources {
    publicResClass = true
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // compose runtime/foundation/ui는 gitit.kmp.library.compose가 이미 넣어준다.
            api(libs.compose.cloudy)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
        }
    }
}
