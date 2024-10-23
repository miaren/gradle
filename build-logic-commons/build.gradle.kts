description = "Provides a set of plugins that are shared between the Gradle and build-logic builds"

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.10")
    }
}

tasks.register("check") {
    dependsOn(subprojects.map { "${it.name}:check" })
}
