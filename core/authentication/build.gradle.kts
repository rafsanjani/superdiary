@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
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
//                implementation(libs.androidx.core.uri)
                implementation("org.jetbrains.androidx.core:core-uri:1.0.0-alpha01")
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
                implementation("androidx.biometric:biometric:1.4.0-alpha04")

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
    namespace = "com.foreverrafs.core.authentication"
}
