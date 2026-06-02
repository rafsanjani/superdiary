@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(conventionLibs.plugins.superdiary.multiplatform.kotlin)
    alias(conventionLibs.plugins.superdiary.android.library)
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
