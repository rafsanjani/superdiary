@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
    // alias(libs.plugins.paparazzi) // Disabled due to incompatibility with the new Android multiplatform plugin
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        androidUnitTest.dependencies {
            implementation(libs.google.testparameterinjector)
            implementation(project(":common-test"))
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.compose)

            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)

            implementation(libs.richTextEditor)

            implementation("com.valentinilk.shimmer:compose-shimmer:1.3.3")
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.kotlinx.serialization.json)

            // project dependencies
            implementation(project(":design-system"))
            implementation(project(":core:location"))
            implementation(project(":common-utils"))
            implementation(project(":core:database"))
            implementation(project(":core:logging"))
            implementation(project(":shared-data"))
            implementation(project(":common-utils"))
            implementation(project(":core:diary-ai"))
            implementation(project(":core:authentication"))
            implementation(project(":core:ui-components"))
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":common-test"))
                implementation(project(":core:database-test"))
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(project(":core:database-test"))
                implementation(libs.assertk.common)
            }
        }
    }
}
