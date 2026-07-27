@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(conventionLibs.plugins.superdiary.multiplatform.kotlin)
    alias(conventionLibs.plugins.superdiary.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.database)
                implementation(libs.kotlin.datetime)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }

        androidHostTest {
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
