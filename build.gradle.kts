plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.compose.multiplatform).apply(false)
    id("org.sonarqube").version("4.4.1.3373")
}

subprojects {
    apply {
        plugin("org.sonarqube")
    }
}

sonar {
    properties {
        property("sonar.projectKey", "rafsanjani_superdiary")
        property("sonar.gradle.skipCompile", "true")
        property("sonar.organization", "rafsanjani")
        property("sonar.token", "79e74e46fb10f72156567174eea10e2afecfec0b")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.androidLint.reportPaths",
            "androidApp/build/reports/lint-results-debug.xml",
        )
        property(
            "sonar.jacoco.reportPath",
            "shared-data/build/kover/bin-reports/testDebugUnitTest.exec",
        )
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/kover/reportDebug.xml"
        )
    }
}

apply {
    from("scripts/git-hooks.gradle.kts")
}

subprojects {
    apply {
        from("${rootDir.path}/quality/static-check.gradle")
    }
}
