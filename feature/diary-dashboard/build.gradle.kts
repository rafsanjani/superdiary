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

            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.compose)

            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)

            implementation(libs.richTextEditor)

            implementation("com.valentinilk.shimmer:compose-shimmer:1.3.2")
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.kotlinx.serialization.json)

            // project dependencies
            implementation(projects.designSystem)
            implementation(projects.core.location)
            implementation(projects.commonUtils)
            implementation(projects.core.logging)
            implementation(projects.sharedData)
            implementation(projects.commonUtils)
            implementation(projects.core.diaryAi)
            implementation(projects.core.authentication)
            implementation(projects.core.uiComponents)
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.commonTest)
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(projects.core.databaseTest)
                implementation(libs.assertk.common)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.dashboard"
}
