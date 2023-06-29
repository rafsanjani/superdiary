@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.mokoResources)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android()

    jvmToolchain(17)

    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(projects.common.data)
                implementation(libs.kotlin.datetime)
                api(libs.moko.resources)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.moko.resources.compose)
                implementation(compose.material3)
                implementation("org.jetbrains.kotlinx:atomicfu:0.21.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            resources.srcDirs("src/androidMain/res")
            resources.srcDirs("src/commonMain/resources")
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.ui"

    compileSdk = libs.versions.compileSdk.get().toInt()

    sourceSets.getByName("main") {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        resources.srcDirs("src/androidMain/res")
        resources.srcDirs("src/commonMain/resources")
        resources.exclude("src/commonMain/resources/MR")
    }

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
