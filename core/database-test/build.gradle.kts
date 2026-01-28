@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core:database"))
                implementation(libs.kotlin.datetime)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }
    }
}

