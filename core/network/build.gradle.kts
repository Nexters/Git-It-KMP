plugins {
    alias(libs.plugins.gitit.jvm.library)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinxJson)

    // OkHttp 엔진은 Android와 데스크톱 양쪽에서 동작하므로 엔진을 하나만 쓴다.
    implementation(libs.ktor.client.okhttp)

    implementation(libs.koin.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.client.mock)
}
