package net.asch.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

class VersionCatalog(project: Project) {
    private val versionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    fun alias(name: String): String = versionCatalog.findPlugin(name).get().get().pluginId
    fun bundle(name: String) = versionCatalog.findBundle(name).get()
    fun version(name: String): String = versionCatalog.findVersion(name).get().requiredVersion
}

fun setupResourceProcessing(project: Project, vararg additionalReplaceProperties: Pair<String, String>) {
    val versionCatalog = VersionCatalog(project)
    project.tasks.withType<ProcessResources>().configureEach {
        val replaceProperties: MutableMap<String, String> = mutableMapOf(
            "minecraft_version" to versionCatalog.version("minecraft"),
            "minecraft_version_range" to "[${versionCatalog.version("minecraft")},)",
            "neo_version" to versionCatalog.version("neoforged-neoforge"),
            "neo_version_range" to "[${versionCatalog.version("neoforged-neoforge")},)",
            "mod_version" to project.version as String,
            "mod_license" to project.propertyString("bulkit.license"),
            "mod_authors" to "Asch",
            *additionalReplaceProperties
        )

        inputs.properties(replaceProperties)
        filesMatching(listOf("META-INF/neoforge.mods.toml")) {
            expand(replaceProperties)
        }
    }
}

fun Project.propertyString(key: String): String = rootProject.properties[key] as String