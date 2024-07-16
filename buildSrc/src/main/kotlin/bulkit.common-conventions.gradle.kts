import net.asch.plugin.alias
import net.asch.plugin.version
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Instant

project.plugins.apply(alias("kotlin-jvm", project))
project.plugins.apply(alias("neoforged-moddev", project))

val baseName = "bulkit"
version = version(baseName, project)

val modId = "$baseName-${project.name}"
project.archivesName = modId

val jvmTarget = JvmTarget.JVM_21
project.extensions.getByType<JavaPluginExtension>().toolchain.languageVersion.set(JavaLanguageVersion.of(jvmTarget.target))
project.extensions.getByType<JavaPluginExtension>().withSourcesJar()

project.tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(jvmTarget)
}

project.tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.FAIL

    manifest {
        attributes(
            "Specification-Title" to modId,
            "Specification-Vendor" to "asch",
            "Specification-Version" to "1",
            "Implementation-Title" to modId,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "asch",
            "Implementation-Timestamp" to Instant.now().toEpochMilli(),
            "Automatic-Module-Name" to "net.asch.$baseName.$name",
        )
    }
}