@file:Suppress("UnusedPrivateProperty")

import com.superdiary.gradle.multiplatform.applyAllMultiplatformTargets


plugins {
    id("com.superdiary.multiplatform.kotlin")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyAllMultiplatformTargets()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.kotlin.datetime)
                kotlin("test")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.assertk.common)
                implementation(kotlin("test"))
            }
        }
    }
}
