@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:logging"))
            implementation(project(":core:diary-ai"))
            implementation(project(":design-system"))
            implementation(project(":core:database"))
            implementation(project(":shared-data"))
            implementation(project(":common-utils"))

            implementation(libs.jetbrains.compose.material3)

            // compose dependencies
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui.backhandler)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.jetbrains.compose.resources)

            // coroutines
            implementation(libs.kotlinx.coroutines.core)

            // koin
            implementation(libs.koin.compose.viewmodel)

            // kotlinx-serialization
            implementation(libs.kotlinx.serialization.json)

            // rich text
            implementation(libs.richTextEditor)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))

            // assertk
            implementation(libs.assertk.common)

            implementation(libs.junit)

            // turbine
            implementation(libs.turbine)

            // coroutines
            implementation(libs.kotlinx.coroutines.test)

            implementation(project(":common-test"))
        }
    }
}
