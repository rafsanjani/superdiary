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
        implementation(libs.jetbrains.compose.foundation)
        implementation(libs.koin.jvm)
        implementation(project(":core:analytics"))
        implementation(project(":core:logging"))
        implementation(project(":core:database"))
        implementation(project(":navigation"))
        implementation(project(":shared-data"))
    }
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}
