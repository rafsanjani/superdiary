// Copyright 2023, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    id("com.superdiary.android.library")
    id("com.superdiary.multiplatform.kotlin")
}

kotlin {
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
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android {
    namespace = "me.saket.swipe"
}
