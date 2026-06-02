@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(conventionLibs.plugins.superdiary.multiplatform.compose)
    alias(conventionLibs.plugins.superdiary.multiplatform.kotlin)
    alias(conventionLibs.plugins.superdiary.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.paparazzi)
}

kotlin {
    sourceSets {
        androidHostTest.dependencies {
            implementation(libs.google.testparameterinjector)
            implementation(projects.commonTest)
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.jetbrains.compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)
            implementation(projects.core.logging)
            implementation(projects.core.uiComponents)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.compose)
            implementation(projects.commonUtils)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)
            implementation(projects.core.authentication)
            implementation(projects.commonUtils)
            implementation(libs.richTextEditor)
            implementation(projects.core.location)
            implementation(libs.jetbrains.compose.navigation3)
            implementation(projects.sharedData)
            implementation(projects.core.database)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(projects.designSystem)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
            implementation(libs.koin.test)
            implementation(projects.core.databaseTest)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.commonTest)
            implementation(libs.assertk.common)
        }
    }
}
