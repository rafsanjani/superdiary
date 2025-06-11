dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    versionCatalogs {
        create("libs") {
            from("io.github.rafsanjani:versions:2025.06.08")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "build-logic"
include(":convention")
