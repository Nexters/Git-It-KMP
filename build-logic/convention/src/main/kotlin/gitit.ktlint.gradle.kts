import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

val libs = the<LibrariesForLibs>()

ktlint {
    version = libs.versions.ktlint.get()
    android = true
    ignoreFailures = false

    filter {
        // 생성된 소스(Compose Resources 등)는 우리가 포맷할 대상이 아니다.
        exclude { it.file.path.contains("${File.separator}build${File.separator}") }
    }
}
