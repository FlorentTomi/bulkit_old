import net.asch.plugin.setupResourceProcessing

plugins {
    id("bulkit.common-conventions")
}

val modId = "bulkit-mekanism"
base.archivesName = modId

neoForge {
    version = libs.versions.neoforged.neoforge
    parchment {
        mappingsVersion = libs.versions.parchment.mappings
        minecraftVersion = libs.versions.parchment.minecraft
    }

    val mod = mods.create(modId) {
        sourceSet(sourceSets.main.get())
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

    unitTest {
        enable()
        testedMod = mod
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

setupResourceProcessing(project, "mekanism_version_range" to "[${libs.versions.mekanism.get()},)")

dependencies {
    implementation(libs.kotlinForForge)
    compileOnly(project(":api"))
    compileOnly(libs.mekanism.api)
    configurations.getByName("localRuntime")(libs.mekanism.core)
    configurations.getByName("localRuntime")(project(":core"))

    testCompileOnly(project(":api"))
    testCompileOnly(libs.mekanism.api)
    testImplementation(libs.bundles.test.impl)
    testRuntimeOnly(libs.bundles.test.runtime)
    testRuntimeOnly(libs.mekanism.core)
    testRuntimeOnly(project(":core"))
}