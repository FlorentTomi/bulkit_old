plugins {
    alias(libs.plugins.neoforged.moddev) apply false
    idea
}

tasks.withType<Wrapper>().configureEach {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL

}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}