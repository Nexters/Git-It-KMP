plugins {
    alias(libs.plugins.gitit.kmp.library.compose)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(projects.core.logging)
            implementation(projects.feature.bookmark)
            implementation(projects.core.permission)
            implementation(projects.feature.home)
            implementation(projects.feature.my)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.projectdetail)
            implementation(projects.feature.projectlist)
            implementation(projects.feature.quiz)
            implementation(projects.core.network)
            implementation(projects.domain)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation3.runtime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.androidx.navigation3.ui)
        }
    }
}
