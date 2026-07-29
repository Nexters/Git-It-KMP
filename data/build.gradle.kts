plugins {
    alias(libs.plugins.gitit.jvm.library)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.network)
    implementation(projects.core.auth)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.koin.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.client.mock)
}
