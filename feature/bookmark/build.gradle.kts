plugins {
    alias(libs.plugins.gitit.kmp.library.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)

            implementation(libs.compose.material3)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
    }
}
