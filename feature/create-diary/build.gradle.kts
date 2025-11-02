@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.mokkery)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        androidUnitTest.dependencies {
            implementation(projects.commonTest)
            implementation(libs.google.testparameterinjector)
        }

        commonMain.dependencies {
            implementation(projects.core.location)
            implementation(projects.core.diaryAi)
            implementation(compose.foundation)
            implementation(compose.preview)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(libs.richTextEditor)
            implementation(projects.designSystem)
            implementation(libs.koin.core)
            implementation(projects.commonUtils)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(projects.sharedData)
            implementation(projects.core.logging)
            implementation(projects.core.permission)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.turbine)
            implementation(libs.assertk.common)
            implementation(projects.commonTest)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.create"
}
