pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://jitpack.io")
        mavenLocal()
    }
    includeBuild("build-logic")
}
System.setProperty("sonar.gradle.skipCompile", "true")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        mavenLocal()
    }

    versionCatalogs {
        create("libs") {
            from("io.github.rafsanjani:versions:2024.06.02")
            // dial these guys down because mockative isn't ready for them yet
            version("kotlin", "1.9.24")
            version("ksp", "1.9.24-1.0.20")
            version("kover", "0.7.6")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "superdiary"
include(":androidApp:app")
include(":androidApp:benchmark")
include(":shared-data")
include(":swipe")
include(":shared-ui")
include(":core:analytics")
include(":core:logging")
include(":core:utils")
include(":desktopApp")
