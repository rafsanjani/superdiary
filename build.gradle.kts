plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.compose.multiplatform).apply(false)
    id("org.sonarqube") version "4.4.1.3373"
}

sonar {
    properties {
        property("sonar.projectKey", "rafsanjani_superdiary")
        property("sonar.organization", "rafsanjani")
        property("sonar.host.url", "https://sonarcloud.io")
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
