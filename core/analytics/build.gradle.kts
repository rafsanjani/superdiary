@file:Suppress("UnusedPrivateProperty")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.io.IOException

plugins {
    alias(libs.plugins.android.library)
    kotlin("multiplatform")
    id("kotlin-parcelize")
    alias(libs.plugins.sonar)
    alias(libs.plugins.testLogger)
    id("com.superdiary.kover")
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
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlin.coroutines.native)
                implementation(libs.kotlin.datetime)
                implementation(libs.touchlab.stately)
                implementation(libs.koin.core)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.kotlin.coroutines.test)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.android)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.assertk.common)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlin.coroutines.core)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.core.analytics"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
