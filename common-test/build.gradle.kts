@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.koin.core)
                implementation(projects.commonUtils)
                implementation(libs.turbine)
            }
        }

        androidMain.dependencies {
            implementation(libs.cashapp.paparazzi)
        }
    }
}

android {
    namespace = "com.foreverrafs.common.test"
}
