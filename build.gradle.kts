plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.compose.multiplatform).apply(false)
    id("org.jetbrains.kotlinx.kover").version("0.7.5")
    id("com.codingfeline.buildkonfig").version("0.15.1").apply(false)
    id("org.sonarqube").version("4.4.1.3373")
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
        "com.foreverrafs.superdiary.database",
        "db",
        "com.foreverrafs.superdiary.ui",
        "com.foreverrafs.superdiary.database",
        "*.buildKonfig",
        "*.di",
        "db",
        "*.*BottomNavigationScreenKt",
        "**/*Activity",
        "**/*App",
        "*.*Screen",
        "*.*ScreenContentKt",
        "*.*ScreenContent*",
        "*.*Preview*",
        "*.*AppKt*",
        "*.components",
        "*.di",
        "*.style",
    )

    properties {
        property("sonar.projectKey", "rafsanjani_superdiary")
        property("sonar.organization", "rafsanjani")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.exclusions", exclusionList)
    }
}

subprojects {
    sonar {
        val reportPath = "${project.buildDir}/reports/kover/reportDebug.xml"
        properties {
            property("sonar.coverage.jacoco.xmlReportPaths", reportPath)
        }
    }
}

apply {
    from("scripts/git-hooks.gradle.kts")
}

subprojects {
    sonar {
        val reportPath = "${project.buildDir}/reports/kover/reportDebug.xml"

        properties {
            property("sonar.projectKey", "rafsanjani_superdiary")
            property("sonar.organization", "rafsanjani")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.coverage.jacoco.xmlReportPaths", reportPath)
        }
    }

    apply {
        from("${rootDir.path}/quality/static-check.gradle")
    }
}
