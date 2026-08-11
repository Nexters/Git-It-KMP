plugins {
    alias(libs.plugins.gitit.jvm.library)
}

dependencies {
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.core)
}
