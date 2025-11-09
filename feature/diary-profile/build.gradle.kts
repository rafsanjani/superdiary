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
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)
            implementation(projects.core.logging)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)
            implementation(projects.core.authentication)
            implementation(projects.commonUtils)
            implementation(projects.sharedData)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(projects.designSystem)
             implementation(compose.materialIconsExtended)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.assertk.common)
            implementation(projects.core.databaseTest)
            implementation(projects.core.database)
            implementation(projects.commonTest)
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.profile"
}
