@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        mavenLocal()
    }
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        mavenLocal()
    }

    versionCatalogs {
        create("libs") {
            from("io.github.rafsanjani:versions:2025.09.14")
            version("compose-multiplatform", "1.9.0-rc01")
            version("mokkery", "2.10.0")
            version("paparazzi", "2.0.0-alpha02")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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
        "*MailManager",
        "*.navigation.*",
    )

    reports {
        includedProjects.add(":shared-data")
        includedProjects.add(":shared-ui")
        includedProjects.add(":core:sync")
        includedProjects.add(":common-utils")
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
include(":shared-ui")

// core project modules
include(":core:authentication")
include(":core:analytics")
include(":core:location")
include(":core:ui-components")
include(":core:logging")
include(":core:database-test")
include(":core:secrets")
include(":core:database")
include(":core:sync")
include(":core:diary-ai")

// common components shared by other modules
include(":common-utils")
include(":common-test")
include(":desktopApp")

// feature modules, a feature is something a user can directly interact with
include(":feature:diary-profile")
include(":feature:diary-auth")
include(":feature:diary-list")
include(":feature:diary-dashboard")

// annotation processor for datasore preferences
include(":preferences:annotation")
include(":preferences:processor")
