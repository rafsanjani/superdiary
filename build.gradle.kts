plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.compose.multiplatform).apply(false)
    alias(libs.plugins.buildKonfig).apply(false)
    alias(libs.plugins.testLogger).apply(false)
    alias(libs.plugins.sonar).apply(false)
    alias(libs.plugins.android.test).apply(false)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.ktlint)
    id("com.superdiary.ktlint")
    id("com.superdiary.githooks")
}
