import net.asch.plugin.setupResourceProcessing

plugins {
    id("bulkit.common-conventions")
}

val modId = "bulkit"
base.archivesName = modId

repositories {
    mavenLocal()
    maven("https://thedarkcolour.github.io/KotlinForForge/")

    maven("https://repo.repsy.io/mvn/asch/main")
}

neoForge {
    version = libs.versions.neoforged.neoforge
    parchment {
        mappingsVersion = libs.versions.parchment.mappings
        minecraftVersion = libs.versions.parchment.minecraft
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            data()
            programArguments.addAll(
                "--mod",
                modId,
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
        }
    }
}

setupResourceProcessing(project)

dependencies {
    implementation(libs.kotlinForForge)
    compileOnly("bulkit:api:0.1.0-alpha")
//    compileOnly(project(":api"))
}