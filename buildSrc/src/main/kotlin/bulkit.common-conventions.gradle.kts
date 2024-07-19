import net.asch.plugin.BulkIt
import net.asch.plugin.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val versionCatalog = VersionCatalog(project)

project.plugins.apply(versionCatalog.alias("kotlin-jvm"))
project.plugins.apply(versionCatalog.alias("neoforged-moddev"))

val jvmTarget = JvmTarget.JVM_21
project.extensions.getByType<JavaPluginExtension>().toolchain.languageVersion.set(JavaLanguageVersion.of(jvmTarget.target))
project.extensions.getByType<JavaPluginExtension>().withSourcesJar()

BulkIt.setup(project)

project.tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(jvmTarget)
}

val localRuntime = project.configurations.create("localRuntime")
configurations.getByName("runtimeClasspath") {
    extendsFrom(localRuntime)
}