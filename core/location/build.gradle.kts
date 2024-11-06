@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    kotlin("multiplatform")
    id("kotlin-parcelize")
    alias(libs.plugins.testLogger)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget()

    iosX64()
    jvm()
    iosArm64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.touchlab.stately)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.core.logging)
                implementation(projects.core.utils)
                implementation(libs.kotlin.inject.runtime)
                implementation(compose.foundation)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.google.playservices.location)
                implementation(libs.moko.permissions.compose)
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
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.moko.permissions.compose)
            }
        }
    }
}

android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    namespace = "com.foreverrafs.core.location"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
