plugins {
    id("bulkit.common-conventions")
    `maven-publish`
}

repositories {
    mavenLocal()
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

neoForge {
    version = libs.versions.neoforged.neoforge
    parchment {
        mappingsVersion = libs.versions.parchment.mappings
        minecraftVersion = libs.versions.parchment.minecraft
    }
}

dependencies {
    implementation(libs.kotlinForForge)
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
//            from(components["java"])
            artifact(tasks.sourcesJar)
        }
    }
}