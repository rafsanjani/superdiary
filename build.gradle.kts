plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.compose.multiplatform).apply(false)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.buildKonfig).apply(false)
    alias(libs.plugins.sonar)
    alias(libs.plugins.testLogger).apply(false)
}

kover {
    useJacoco("0.8.10")
}

koverReport {
    filters {
        excludes {
            packages(
                "com.foreverrafs.superdiary.database",
                "db",
                "com.foreverrafs.superdiary.ui",
            )
            files("BottomNavigationScreenKt")
        }
    }
}

sonar {
    val exclusionList = listOf(
        "**/*Preview*",
        // Compose screens
        "**/*Screen*",
        // Koin modules and all that shit
        "**/di/*",
        // iOS view controller
        "**/ui/ViewController*",
        // The entry-point UI of the app
        "**/App.kt",
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
    )

    properties {
        property("sonar.projectKey", "rafsanjani_superdiary")
        property("sonar.organization", "rafsanjani")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.exclusions", exclusionList)
    }
}

apply {
    from("scripts/git-hooks.gradle.kts")
}

subprojects {
    sonar {
        val buildDir = project.layout.buildDirectory.asFile.get()
        val reportPath = "$buildDir/reports/kover/reportDebug.xml"
        val lintReportPath = "$buildDir/reports/lint-results-debug.xml"

        properties {
            property("sonar.projectKey", "rafsanjani_superdiary")
            property("sonar.organization", "rafsanjani")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.androidLint.reportPaths", lintReportPath)
            property("sonar.coverage.jacoco.xmlReportPaths", reportPath)
        }
    }

    apply {
        from("${rootDir.path}/quality/static-check.gradle")
    }
}
