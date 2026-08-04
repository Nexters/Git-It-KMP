plugins {
    alias(libs.plugins.gitit.jvm.library)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.network)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.client.mock)
}
