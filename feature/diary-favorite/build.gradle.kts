@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        androidUnitTest.dependencies {
            implementation(project(":common-test"))
            implementation(libs.google.testparameterinjector)
        }

        commonMain.dependencies {
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.kotlin.datetime)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.richTextEditor)
            implementation(project(":design-system"))
            implementation(libs.koin.core)
            implementation(project(":common-utils"))
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(project(":shared-data"))

            implementation(project(":core:location"))
            implementation(project(":core:diary-ai"))
            implementation(project(":core:database"))
            implementation(project(":core:ui-components"))
            implementation(project(":core:logging"))
            implementation(project(":core:permission"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.turbine)
            implementation(libs.assertk.common)
            implementation(project(":core:database-test"))
            implementation(project(":common-test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

