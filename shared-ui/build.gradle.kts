@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    kotlin("multiplatform")
    id("kotlin-parcelize")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    android() // Because paparazzi plugin hasn't been upgraded to support androidTarget() yet

    jvm()
    jvmToolchain(17)

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            linkerOpts += "-lsqlite3"
        }
    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("DataObjects")
    }

    sourceSets {
        commonMain {
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(projects.sharedData)
                implementation(libs.kotlin.datetime)
                implementation(libs.koin.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.koin.compose)
                implementation(libs.richTextEditor)
                implementation("co.touchlab:stately-common:2.0.6")
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.cashapp.paparazzi)
                implementation(libs.koin.android)
                implementation(libs.koin.test)
                implementation(libs.mockk)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.koin.jvm)
                implementation(libs.kotlin.coroutines.swing)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.ui.tooling)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.shared"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets["main"].res.srcDirs("src/commonMain/resources")
}
