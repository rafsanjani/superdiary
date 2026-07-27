@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(conventionLibs.plugins.superdiary.multiplatform.kotlin)
    alias(conventionLibs.plugins.superdiary.android.library)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
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
