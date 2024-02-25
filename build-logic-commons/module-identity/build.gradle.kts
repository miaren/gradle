plugins {
    `kotlin-dsl`
}

description = "Provides a plugin to define the version and name for subproject publications"

group = "gradlebuild"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    api(platform(project(":build-platform")))

    implementation(project(":basics"))

    implementation("com.google.code.gson:gson")
}
