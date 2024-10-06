@file:Suppress("UnusedPrivateProperty")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    kotlin("multiplatform")
    id("com.superdiary.kover")
    id("dev.mokkery") version "2.3.0"
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
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-opt-in=com.aallam.openai.api.BetaOpenAI",
            "-Xskip-prerelease-check",
        )
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.touchlab.stately)
                implementation(libs.koin.core)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.openAiKotlin)
                implementation(libs.uuid)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.androidx.datastore.okio)
                runtimeOnly(libs.ktor.client.cio)

                // Project dependencies
                implementation(projects.core.utils)
                implementation(projects.core.analytics)
                implementation(projects.core.logging)
                implementation(projects.core.location)
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
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.assertk.common)
            }
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }

        iosMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlinx.coroutines.core)
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

buildkonfig {
    packageName = "com.foreverrafs.superdiary.buildKonfig"

    val openAiKey =
        project.findProperty("openAiKey")?.toString()

    if (openAiKey == null) {
        println("WARN: OpenAI key not provided!")
    }

    // default config is required
    defaultConfigs {
        buildConfigField(STRING, "openAIKey", openAiKey ?: "")
    }
}
