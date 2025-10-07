import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.compose.hot-reload")
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm()
    sourceSets.commonMain.dependencies {
        implementation(compose.desktop.currentOs)
        implementation(compose.foundation)
        implementation(libs.koin.jvm)
        implementation(projects.core.analytics)
        implementation(projects.core.logging)
        implementation(projects.core.database)
        implementation(projects.sharedUi)
        implementation(projects.sharedData)
    }
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}
