@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
    // alias(libs.plugins.paparazzi) // Disabled due to incompatibility with the new Android multiplatform plugin
}

kotlin {
    sourceSets {
        androidUnitTest.dependencies {
            implementation(libs.google.testparameterinjector)
            implementation(project(":common-test"))
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":common-utils"))
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)
            implementation(project(":core:location"))
            implementation(libs.richTextEditor)
            implementation(project(":shared-data"))
            implementation(libs.shimmer)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(project(":design-system"))
            implementation(libs.kotlinx.serialization.json)

            implementation(compose.materialIconsExtended)
        }

        commonTest.dependencies {
            implementation(libs.junit)
            implementation(project(":core:database-test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(project(":common-test"))
            implementation(libs.assertk.common)
        }
    }
}
