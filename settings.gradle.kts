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
            from("io.github.rafsanjani:versions:2024.10.27")
            version("compose-multiplatform", "1.7.0")
            version("paparazzi", "1.3.5-SNAPSHOT")
            version("openaiKotlin", "4.0.0-SNAPSHOT")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.8.3"
}

kover {
    enableCoverage()

    val classes = listOf(
        "**/*Preview*",
        // Compose screens
        "**/*Screen*",
        "**/*Tab*",
        // Koin modules and all that shit
        "**/di/*",
        // iOS view controller
        "**/ui/ViewController*",
        // The entry-point UI of the app
        "**/AppKt",
        "**/AppKt*",
        // The android application class
        "**/DiaryApp.kt",
        // JVM App entrypoint
        "**/Main.kt",
        // SqlDelight database driver
        "**/*DatabaseDriver*",
        // Sqlite Database file
        "**/*Database",
        // Reusable screen components
        "**/components/**",
        "**/*Resources*",
        "*.*.*ComposableSingletons*",
        "**/app/**",
        "**/androidApp/**",
        "**/DiaryListActions",
        "*Tab*",
        "*App*",
        "*AndroidPreviews*",
        "*BackPressHandler*",
        "*SnapshotTheme*",
        "*BottomNavigationScreen*",
        "*Screen*",
        "*DiaryListActions*",
        "*DiaryFilters*",
        "*DiarySortCriteria*",
        "*BuildKonfig*",
        "*DatabaseDriver*",
        "Modules*",
        "*Theme*",
        "*DiaryFilterSheet*",
        "*DiaryHeader*",
        "*DiarySearchBar*",
        "*DiarySelectionModifierBar*",
        "db.*",
        "*di.*",
        "*generated.resources.*",
        "*components.*",
    )

    reports {
        includedProjects.add(":shared-data")
        includedProjects.add(":shared-ui")

        excludedClasses.addAll(classes)
        excludesAnnotatedBy.add("androidx.compose.runtime.Composable")
    }
}

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
include(":core:secrets")
include(":desktopApp")
