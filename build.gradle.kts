import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    eclipse
    idea

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.neoforged.moddev)
}

tasks.withType<Wrapper>().configureEach {
    gradleVersion = BuildConstants.GRADLE_VERSION
    distributionType = Wrapper.DistributionType.BIN
}

repositories {
    mavenLocal()
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

base.archivesName = ModConstants.ID

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_21)

neoForge {
    version = libs.versions.neoforged.neoforge
    parchment {
        mappingsVersion = libs.versions.parchment.mappings
        minecraftVersion = libs.versions.parchment.minecraft
    }

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", ModConstants.ID)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", ModConstants.ID)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", ModConstants.ID)
        }

        create("data") {
            data()
            programArguments.addAll(
                "--mod", ModConstants.ID,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(ModConstants.ID) {
            sourceSet(sourceSets.main.get())
        }

        dependencies {
            implementation(libs.kotlinForForge)
        }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

configurations.create("localRuntime").extendsFrom(configurations.runtimeClasspath.get())

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties: Map<String, String> = mapOf(
        "minecraft_version" to libs.versions.minecraft.get(),
        "minecraft_version_range" to "[${libs.versions.minecraft.get()},)",
        "neo_version" to libs.versions.neoforged.neoforge.get(),
        "neo_version_range" to "[${libs.versions.neoforged.neoforge.get()},)",
        "mod_license" to ModConstants.LICENSE,
        "mod_authors" to ModConstants.AUTHORS
    )

    inputs.properties(replaceProperties)
    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(replaceProperties)
    }
}

ModConstants.EXTENSIONS.forEach(::createModExtension)

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

fun createModExtension(name: String) {
    val sourceSet = sourceSets.create(name)
    val id = "${ModConstants.ID}_name"

    neoForge.mods.create(id) {
        sourceSet(sourceSets.main.get())
        sourceSet(sourceSet)
    }
}
