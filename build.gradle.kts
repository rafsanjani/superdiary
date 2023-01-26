plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.ktlint).apply(false)
}

apply {
    from("scripts/git-hooks.gradle.kts")
}

subprojects {
    apply {
        from("../scripts/detekt.gradle")
        from("../scripts/ktlint.gradle")
    }
}