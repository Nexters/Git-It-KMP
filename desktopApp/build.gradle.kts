import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.gitit.jvm.compose)
}

compose.desktop {
    application {
        mainClass = "com.nexters.hytime.gitit.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.nexters.hytime.gitit"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(projects.core.logging)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}
