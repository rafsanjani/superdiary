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
            from("io.github.rafsanjani:versions:2026.03.01")
            // Because 3.2.4 is making internal calls to android.util.Log which dey borst my mind
            version("supabase", "3.2.3")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.7"
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
        "*Result*",
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
// commenting this out because it doesn't work with agp 9.0 and the new android multiplatform plugin
// include(":androidApp:benchmark")
include(":shared-data")
include(":navigation")
include(":umbrella")

// core project modules
include(":core:authentication")
include(":core:analytics")
include(":core:location")
include(":core:ui-components")
include(":core:logging")
include(":core:database-test")
include(":core:secrets")
include(":core:database")
include(":core:permission")
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
include(":feature:create-diary")
include(":feature:diary-favorite")
include(":feature:diary-chat")

// annotation processor for datasore preferences
include(":preferences:annotation")
include(":preferences:processor")
