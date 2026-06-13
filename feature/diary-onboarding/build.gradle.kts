@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        androidHostTest.dependencies {
            implementation(libs.google.testparameterinjector)
            implementation(projects.commonTest)
        }

        commonMain.dependencies {
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(projects.designSystem)
            implementation(projects.core.uiComponents)
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk.common)
            }
        }
    }
}
