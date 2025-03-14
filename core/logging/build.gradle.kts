@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.touchlab.stately)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.touchlab.kermit)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.sentry.android)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.assertk.common)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.core.logging"
}
