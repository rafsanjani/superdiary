plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.compose.multiplatform).apply(false)
    id("com.codingfeline.buildkonfig").version("0.15.1").apply(false)
}

apply {
    from("scripts/git-hooks.gradle.kts")
}

subprojects {
    apply {
        from("${rootDir.path}/quality/static-check.gradle")
    }
}
