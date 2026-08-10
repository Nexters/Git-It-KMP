import org.gradle.api.tasks.Exec

plugins {
    alias(libs.plugins.gitit.kmp.library.compose)
}

val macOsBridgeSource = layout.projectDirectory.file("src/jvmMain/native/macos/NotificationPermissionBridge.m")
val macOsBridgeCompileScript = layout.projectDirectory.file("scripts/compile-macos-bridge.sh")
val generatedNativeResources = layout.buildDirectory.dir("generated/permissionNativeResources/jvmMain")
val macOsBridgeLibrary = generatedNativeResources.map { it.file("native/macos/libgitit_permission.dylib") }
val isMacOs = providers.systemProperty("os.name").map { it.startsWith("Mac", ignoreCase = true) }

val compileMacOsPermissionBridge =
    tasks.register<Exec>("compileMacOsPermissionBridge") {
        group = "build"
        description = "macOS 알림 권한 네이티브 브리지를 빌드한다."
        inputs.files(macOsBridgeSource, macOsBridgeCompileScript)
        outputs.file(macOsBridgeLibrary)
        enabled = isMacOs.get()
        commandLine(
            "/bin/sh",
            macOsBridgeCompileScript.asFile.absolutePath,
            macOsBridgeSource.asFile.absolutePath,
            macOsBridgeLibrary.get().asFile.absolutePath,
        )
    }

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        jvmMain {
            resources.srcDir(generatedNativeResources)
            dependencies {
                implementation(libs.jna)
            }
        }
    }
}

tasks.named("jvmProcessResources") {
    dependsOn(compileMacOsPermissionBridge)
}
