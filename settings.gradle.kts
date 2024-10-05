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
            from("io.github.rafsanjani:versions:2024.10.06")
            version("compose-multiplatform", "1.7.0-rc01")
            version("paparazzi", "1.3.5-SNAPSHOT")
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
include(":core:location")
include(":core:logging")
include(":core:utils")
include(":desktopApp")
