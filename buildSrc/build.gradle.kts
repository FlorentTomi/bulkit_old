plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.neoforged.net/releases")
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("gradle-plugin"))
}