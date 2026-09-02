plugins {
    id("io.kotzilla.kotzilla-plugin")
}

subprojects {
    tasks.matching { it.name.startsWith("runKtlint") }.configureEach {
        dependsOn(tasks.matching { it.name.startsWith("generateKotzillaConfig") })
    }
}
