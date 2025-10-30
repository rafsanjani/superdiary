@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.paparazzi)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        androidUnitTest.dependencies {
            implementation(libs.google.testparameterinjector)
            implementation(projects.commonTest)
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.preview)
            implementation(compose.materialIconsExtended)
            implementation(projects.core.logging)
            implementation(libs.androidx.core.uri)
            implementation("androidx.navigationevent:navigationevent:1.0.0-beta01")
            implementation("org.jetbrains.androidx.navigationevent:navigationevent-compose:1.0.0-SNAPSHOT+update-templates-demos")
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)
            implementation(projects.core.authentication)
            implementation(projects.commonUtils)
            implementation(projects.sharedData)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(projects.designSystem)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
            implementation(libs.koin.test)
            implementation(projects.commonTest)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.assertk.common)
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.auth"
}
