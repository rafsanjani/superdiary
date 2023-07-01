@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    alias(libs.plugins.mokoResources)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvmToolchain(17)

    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "common"
            linkerOpts("-lsqlite3")
            export(projects.common.data)
            export(projects.common.ui)

            binaryOption("bundleId", "com.foreverrafs.superdiary.common")

            extraSpecAttributes["resources"] =
                "['src/commonMain/resources/**', 'src/iosMain/resources/**']"

            extraSpecAttributes["exclude_files"] = "['src/commonMain/resources/MR/**']"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.common.ui)
                api(projects.common.data)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.compileSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
