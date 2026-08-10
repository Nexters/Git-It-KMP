rootProject.name = "Git-It-KMP"

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// 모듈 의존성을 문자열 대신 `projects.core.designsystem` 형태로 참조한다.
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")
include(":desktopApp")
include(":shared")

include(":feature:home")
include(":feature:onboarding")

include(":feature:my")
include(":feature:projectdetail")
include(":feature:projectlist")

include(":core:designsystem")
include(":core:network")
include(":core:logging")
include(":core:auth")
include(":core:permission")
include(":domain")
include(":data")
