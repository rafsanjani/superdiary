@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
    // alias(libs.plugins.paparazzi)
}

kotlin {
    sourceSets {
        androidUnitTest.dependencies {
            implementation(libs.google.testparameterinjector)
            implementation(projects.commonTest)
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.commonUtils)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)
            implementation(projects.core.location)
            implementation(libs.richTextEditor)
            implementation(projects.sharedData)
            implementation(libs.shimmer)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(projects.designSystem)
            implementation(libs.kotlinx.serialization.json)

            implementation(compose.materialIconsExtended)
        }

        commonTest.dependencies {
            implementation(libs.junit)
            implementation(projects.core.databaseTest)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.commonTest)
            implementation(libs.assertk.common)
        }
    }
}
