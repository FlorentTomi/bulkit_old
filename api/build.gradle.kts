plugins {
    id("bulkit.common-conventions")
    `maven-publish`
}

neoForge {
    version = libs.versions.neoforged.neoforge
    parchment {
        mappingsVersion = libs.versions.parchment.mappings
        minecraftVersion = libs.versions.parchment.minecraft
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.repsy.io/mvn/asch/main")
            credentials {
                username = "asch"
                password = rootProject.properties["publish.maven.pw"] as String
            }
        }
    }

    publications {
        register<MavenPublication>("mavenApi") {
            artifact(tasks.sourcesJar)
        }
    }
}