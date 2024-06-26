import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.neoforged.moddev)
    alias(libs.plugins.kotlin.jvm)
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

sourceSets {
    main {
        resources {
            srcDir("src/dataGen/${ModConstants.ID}/resources")
            exclude(".cache")
        }
    }
}

val apiSourceSet = sourceSets.create("api")

neoForge {
    version = libs.versions.neoforged.neoforge
    parchment {
        mappingsVersion = libs.versions.parchment.mappings
        minecraftVersion = libs.versions.parchment.minecraft
    }

    addModdingDependenciesTo(apiSourceSet)

    mods {
        create(ModConstants.ID) {
            sourceSet(apiSourceSet)
            sourceSet(sourceSets.main.get())
        }
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
                "--output", file("src/dataGen/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
            mods.add(neoForge.mods[ModConstants.ID])
        }
    }
}

dependencies {
    implementation(apiSourceSet.output)
    implementation(libs.kotlinForForge)
}

ModConstants.EXTENSIONS.forEach(::createModExtension)

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

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

fun createModExtension(name: String) {
    val id = "${ModConstants.ID}_$name"

    val sourceSet = sourceSets.create(name) {
        compileClasspath += apiSourceSet.output
        resources {
            srcDir("src/dataGen/${name}/resources")
            exclude(".cache")
        }
    }
    // This enables IntelliJ to know that you need the addons when you run the main run
    sourceSets["main"].runtimeClasspath += sourceSet.output

    val mod = neoForge.mods.create(id) {
        sourceSet(sourceSet)
    }

    val buildJar = tasks.register<Jar>(sourceSet.jarTaskName) {
        group = "build"
        archiveClassifier = name
        from(sourceSet.output)
    }
    tasks.getByName("assemble").dependsOn(buildJar)

    neoForge {
        addModdingDependenciesTo(sourceSet)

        runs["data"].programArguments.addAll(
            "--mod", id,
            "--existing", file("src/$name/resources/").absolutePath
        )

        runs.configureEach {
            mods.add(mod)
        }
    }
}
