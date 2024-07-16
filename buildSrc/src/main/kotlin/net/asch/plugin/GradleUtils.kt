package net.asch.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

fun alias(name: String, project: Project): String {
    val pluginDep = project.extensions.getByType<VersionCatalogsExtension>().named("libs").findPlugin(name).get().get()
    return pluginDep.pluginId
}

fun version(name: String, project: Project): String =
    project.extensions.getByType<VersionCatalogsExtension>().named("libs").findVersion(name).get().requiredVersion

fun setupResourceProcessing(project: Project, vararg additionalReplaceProperties: Pair<String, String>) {
    project.tasks.withType<ProcessResources>().configureEach {
        val replaceProperties: MutableMap<String, String> = mutableMapOf(
            "minecraft_version" to version("minecraft", project),
            "minecraft_version_range" to "[${version("minecraft", project)},)",
            "neo_version" to version("neoforged-neoforge", project),
            "neo_version_range" to "[${version("neoforged-neoforge", project)},)",
            "mod_version" to project.version as String,
            "mod_license" to project.propertyString("bulkit.license"),
            "mod_authors" to "Asch", *additionalReplaceProperties
        )

        inputs.properties(replaceProperties)
        filesMatching(listOf("META-INF/neoforge.mods.toml")) {
            expand(replaceProperties)
        }
    }
}

fun Project.propertyString(key: String): String = rootProject.properties[key] as String