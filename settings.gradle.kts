plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("net.neoforged.moddev.repositories") version "0.1.99"
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.neoforged.net/releases")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("https://maven.blamejared.com/")
        maven("https://modmaven.dev/")
        mavenCentral()
    }
}

rootProject.name = "bulkit"
include(":api", ":core", ":extensions:mekanism")