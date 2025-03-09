@file:Suppress("UnstableApiUsage")

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
            from("io.github.rafsanjani:versions:2025.03.09")
            version("compose-multiplatform", "1.8.0-alpha03")
            // Because the all versions newer than this do not play well with compose 1.8.0-alpha03
            version("richTextEditor", "1.0.0-rc09")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.1"
}

kover {
    enableCoverage()

    val classes = listOf(
        "**/*Preview*",
        // Compose screens
        "**/*Tab*",
        // Koin modules and all that shit
        "**/di/*",
        // iOS view controller
        "**/ui/ViewController*",
        // The entry-point UI of the app
        "**/AppKt",
        // The android application class
        "**/DiaryApp.kt",
        // JVM App entrypoint
        "**/Main.kt",
        // SqlDelight database driver
        "**/*DatabaseDriver*",
        // Sqlite Database file
        "**/*Database",
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
        "*BottomNavigationRoute*",
        "*Dto*",
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
        "*AndroidDataStorePathResolver",
        "*.*NavType",
        "*.RemoteDataSource*",
        "db.*",
        "*di.*",
        "*screen.*",
        "*components.*",
        "*utils.FileSystem_androidKt",
        "*generated.resources.*",
        "*components.*",
    )

    reports {
        includedProjects.add(":shared-data")
        includedProjects.add(":shared-ui")
        includedProjects.add(":core:sync")
        includedProjects.add(":feature:diary-profile")
        includedProjects.add(":feature:diary-list")
        includedProjects.add(":feature:diary-auth")
        includedProjects.add(":feature:diary-ai")

        excludedClasses.addAll(classes)
        excludesAnnotatedBy.add("androidx.compose.runtime.Composable")

        verify {
            rule {
                name = "Minimum Coverage Rule"
                bound {
                    minValue = 92
                }
            }
        }
    }
}

rootProject.name = "superdiary"
include(":androidApp:app")
include(":design-system")
include(":androidApp:benchmark")
include(":shared-data")
include(":swipe")
include(":shared-ui")
include(":core:authentication")
include(":core:analytics")
include(":core:location")
include(":core:logging")
include(":common-utils")
include(":core:sync")
include(":common-test")
include(":core:secrets")
include(":core:database")
include(":core:database-test")
include(":desktopApp")
include(":feature:diary-ai")
include(":feature:diary-profile")
include(":feature:diary-auth")
include(":feature:diary-list")
include(":preferences:annotation")
include(":preferences:processor")
