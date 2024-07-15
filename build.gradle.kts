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
    maven("https://modmaven.dev/")
}

data class ExtensionDependencies(
    val version: String,
    val apiProvider: Provider<MinimalExternalModuleDependency>,
    val runtimeProviders: List<Provider<MinimalExternalModuleDependency>>
)

val extensionDependencies = mapOf(
    "mekanism" to ExtensionDependencies(libs.versions.mekanism.get(), libs.mekanism.api, listOf(libs.mekanism.core))
)

base.archivesName = ModConstants.ID

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_21)

val apiSourceSet = sourceSets.create("api")
sourceSets {
    main {
        resources {
            srcDir("src/dataGen/${ModConstants.ID}/resources")
            exclude(".cache")
        }
    }
}

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
                "--mod",
                ModConstants.ID,
                "--all",
                "--output",
                file("src/dataGen/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
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
    implementation(libs.kotlinForForge)
    compileOnly(apiSourceSet.output)
}

ModConstants.EXTENSIONS.forEach(::createModExtension)

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties: Map<String, String> = mapOf(
        "minecraft_version" to libs.versions.minecraft.get(),
        "minecraft_version_range" to "[${libs.versions.minecraft.get()},)",
        "neo_version" to libs.versions.neoforged.neoforge.get(),
        "neo_version_range" to "[${libs.versions.neoforged.neoforge.get()},)",
        "mod_version" to libs.versions.bulkit.get(),
        "mod_license" to ModConstants.LICENSE,
        "mod_authors" to ModConstants.AUTHORS
    ) + extensionDependencies.map { (depName, dep) ->
        "${depName}_version_range" to "[${dep.version},)"
    }

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
        compileClasspath += sourceSets.main.get().output
        resources {
            srcDir("src/dataGen/${name}/resources")
            exclude(".cache")
        }
    }

    sourceSets.main.get().runtimeClasspath += sourceSet.output

    val mod = neoForge.mods.create(id) {
        sourceSet(apiSourceSet)
        sourceSet(sourceSet)
    }

    val buildJar = tasks.register<Jar>(sourceSet.jarTaskName) {
        group = "build"
        archiveClassifier = name
        from(sourceSet.output)
    }
    tasks.getByName("assemble").dependsOn(buildJar)

    dependencies {
        configurations.getByName("${name}Implementation")(libs.kotlinForForge)
        configurations.getByName("${name}CompileOnly")(apiSourceSet.output)

        extensionDependencies[name]?.let { extDep ->
            configurations.getByName("${name}CompileOnly")(extDep.apiProvider)
            extDep.runtimeProviders.forEach { runtimeOnly(it) }
        }
    }

    neoForge {
        addModdingDependenciesTo(sourceSet)

        runs["data"].programArguments.addAll(
            "--mod", id, "--existing", file("src/$name/resources/").absolutePath
        )

        runs.configureEach {
            mods.add(mod)
        }
    }
}
