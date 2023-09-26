@file:Suppress("UnusedPrivateProperty")

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    alias(libs.plugins.mokoResources)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvmToolchain(17)

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
            linkerOpts += "-lsqlite3"

            if (System.getenv("XCODE_VERSION_MAJOR") == "1500") {
                linkerOpts += "-ld64"
            }

            export(projects.common.data)
            export(projects.common.ui)
            isStatic = true
            binaryOption("bundleId", "com.foreverrafs.superdiary.common")
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
