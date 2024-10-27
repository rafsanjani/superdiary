@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.ksp)
    id("com.superdiary.secrets")
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
                implementation(compose.foundation)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.touchlab.stately)
                implementation(libs.koin.core)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.touchlab.kermit)
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
    namespace = "com.foreverrafs.core.utils"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
