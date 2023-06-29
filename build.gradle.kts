@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.compose.multiplatform).apply(false)
    alias(libs.plugins.mokoResources).apply(false)
}

apply {
    from("scripts/git-hooks.gradle.kts")
}

subprojects {
    apply {
        from("${rootDir.path}/scripts/detekt.gradle")
    }
}
