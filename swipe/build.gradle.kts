// Copyright 2023, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("UnusedPrivateProperty")

plugins {
    kotlin("multiplatform")
    id("com.superdiary.kover")
    alias(libs.plugins.sonar)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    androidTarget()

    iosX64()
    jvm()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk.common)
                implementation(libs.junit)
                implementation(libs.turbine)
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}

android {
    namespace = "me.saket.swipe"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
