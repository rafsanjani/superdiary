@file:Suppress("UnusedPrivateProperty")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.io.IOException

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
    kotlin("multiplatform")
    id("kotlin-parcelize")
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.sonar)
    alias(libs.plugins.mockmp)
    alias(libs.plugins.testLogger)
    id("com.superdiary.kover")
}

buildkonfig {
    packageName = "com.foreverrafs.superdiary.buildKonfig"

    val openAiKey =
        project.findProperty("openAiKey") ?: throw IOException("OpenAI API key not provided!")

    // default config is required
    defaultConfigs {
        buildConfigField(STRING, "openAIKey", openAiKey.toString())
    }
}

sqldelight {
    databases.register("SuperDiaryDatabase") {
        packageName.set("com.foreverrafs.superdiary.database")
        deriveSchemaFromMigrations.set(true)
    }
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
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.openAiKotlin)
                implementation(libs.touchlab.kermit)
                runtimeOnly(libs.ktor.client.cio)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.android)
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.koin.android)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
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
                implementation(libs.mockmp.runtime)
            }
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }

        iosMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.ktor.client.darwin)
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

mockmp {
    usesHelper = true
    installWorkaround()
}
