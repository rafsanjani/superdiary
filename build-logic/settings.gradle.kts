dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    versionCatalogs {
        create("libs") {
            from("io.github.rafsanjani:versions:2024.02.14")
            version("compose-multiplatform", "1.6.0-dev1369")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "build-logic"
include(":convention")
