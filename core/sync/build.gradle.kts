@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.mokkery)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.koin.core)
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.lifecycle.runtime.compose)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.core.logging)
                implementation(projects.commonUtils)
                implementation(libs.kotlinx.coroutines.core)
                implementation(projects.sharedData)
                implementation(projects.core.location)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.assertk.common)
                implementation(projects.commonTest)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.moko.permissions.compose)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.core.sync"
}
