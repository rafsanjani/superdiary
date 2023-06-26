import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
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
            isStatic = true

            export(projects.common.data)
            export(projects.common.ui)

            binaryOption("bundleId", "com.foreverrafs.superdiary.common")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.data)
            }
        }

        val iosMain by getting {
            dependencies {
                api(projects.common.data)
                api(projects.common.ui)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.kmmcocoapods"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.compileSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}
