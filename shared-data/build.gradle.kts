@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    kotlin("multiplatform")
    alias(libs.plugins.mokkery)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget()

    jvm()
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.forEach { binary ->
            binary.linkerOpts += "-lsqlite3"
        }
    }

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
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.openAiKotlin)
                implementation(libs.uuid)
                implementation(projects.preferences.annotation)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.androidx.datastore.okio)
                implementation(libs.ktor.client.cio)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.supabase.posgrest)
                implementation(libs.supabase.realtime)

                // Project dependencies
                implementation(projects.commonUtils)
                implementation(projects.core.analytics)
                implementation(projects.core.secrets)
                implementation(projects.core.logging)
                implementation(projects.core.location)
                implementation(projects.core.database)
            }

            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }

        androidMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.android)
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.koin.android)
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
                implementation(projects.core.databaseTest)
                implementation(projects.commonTest)
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

dependencies {
    add("kspCommonMainMetadata", projects.preferences.processor)
}

afterEvaluate {
    tasks {
        withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>> {
            if (name != "kspCommonMainKotlinMetadata") {
                dependsOn("kspCommonMainKotlinMetadata")
            }
        }
    }
}
