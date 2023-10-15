@file:Suppress("UnusedPrivateProperty")

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases.register("SuperDiaryDatabase") {
        packageName.set("com.foreverrafs.superdiary.database")
        deriveSchemaFromMigrations.set(true)
    }
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android()

    ios()
    iosX64()
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlin.coroutines.native)
                implementation(libs.kotlin.datetime)
                implementation(libs.koin.core)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.square.sqldelight.coroutinesExt)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.square.sqldelight.driver.android)
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.koin.android)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.assertk.common)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.koin.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
