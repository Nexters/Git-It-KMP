plugins {
    alias(libs.plugins.gitit.jvm.library)
}

dependencies {
    implementation(projects.core.auth)
}
