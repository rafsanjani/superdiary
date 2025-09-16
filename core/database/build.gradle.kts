@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases.register("SuperDiaryDatabase") {
        packageName.set("com.foreverrafs.superdiary.database")
        deriveSchemaFromMigrations.set(true)
    }
    linkSqlite.set(true)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.koin.core)
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.core.logging)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.android)
                implementation(libs.square.sqldelight.coroutinesExt)
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
                implementation(libs.ktor.client.ios)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.core.database"
}
