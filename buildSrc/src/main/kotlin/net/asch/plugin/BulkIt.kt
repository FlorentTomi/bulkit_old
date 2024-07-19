package net.asch.plugin

import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.time.Instant

object BulkIt {
    private const val MOD_ID = "bulkit"
    private val SUBPROJECTS = mapOf(
        "api" to Subproject("$MOD_ID-api"), "core" to Subproject(MOD_ID), "mekanism" to Subproject("$MOD_ID-mekanism")
    )

    fun modId(project: Project): String = SUBPROJECTS[project.name]?.modId ?: ""

    fun setup(project: Project) {
        val subproject = SUBPROJECTS[project.name] ?: return
        val versionCatalog = VersionCatalog(project)

        project.archivesName.set(subproject.modId)
        project.version = versionCatalog.version(subproject.modId)
        println("${project.name}: ${project.version}")

        if (project.name != "api") {
            val localRuntime = project.configurations.create("localRuntime")
            project.configurations.getByName("runtimeClasspath") {
                extendsFrom(localRuntime)
            }

            project.dependencies {
                project.configurations.getByName("compileOnly")(versionCatalog.bundle("extra-api"))
                localRuntime(versionCatalog.bundle("extra-runtime"))
            }
        }

        project.tasks.withType<Jar> {
            duplicatesStrategy = DuplicatesStrategy.FAIL

            manifest {
                val manifestAttributes = mutableMapOf(
                    "Specification-Title" to subproject.modId,
                    "Specification-Vendor" to "asch",
                    "Specification-Version" to "1",
                    "Implementation-Title" to subproject.modId,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "asch",
                    "Implementation-Timestamp" to Instant.now().toEpochMilli(),
                    "Automatic-Module-Name" to "net.asch.$MOD_ID.${project.name}"
                )

                if (project.name == "api") {
                    manifestAttributes["FMLModType"] = "GAMELIBRARY"
                }

                attributes(manifestAttributes)
            }
        }
    }

    private data class Subproject(val modId: String)
}
