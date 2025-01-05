@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.parcelize)
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
                implementation(compose.foundation)
                implementation(libs.kotlinx.coroutines.test)

                implementation(dependencies.platform(libs.supabase.bom))
                implementation(libs.supabase.posgrest)
                implementation(libs.androidx.core.uri)
                implementation(libs.supabase.auth)
                implementation(libs.supabase.realtime)
                implementation(libs.supabase.compose.auth)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.json)
                implementation(projects.commonUtils)
                implementation(projects.core.secrets)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.google.playservices.location)
                implementation(libs.moko.permissions.compose)
                implementation(libs.ktor.client.json)

                // this isn't a bom
                implementation(libs.androidx.credentials.bom)
                implementation(libs.androidx.credentials.playServicesAuth)
                implementation(libs.google.playservices.identity)
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
                implementation(libs.ktor.client.ios)
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

    namespace = "com.foreverrafs.core.authentication"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
