@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.parcelize)
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

        androidUnitTest {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
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
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
