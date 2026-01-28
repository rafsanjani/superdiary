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
            implementation(libs.koin.core)
            implementation(libs.jetbrains.compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)
            implementation(project(":core:logging"))
            implementation(project(":core:ui-components"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.compose)
            implementation(project(":common-utils"))
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)
            implementation(project(":core:authentication"))
            implementation(project(":common-utils"))
            implementation(libs.richTextEditor)
            implementation(project(":core:location"))
            implementation(libs.jetbrains.compose.navigation3)
            implementation(project(":shared-data"))
            implementation(project(":core:sync"))
            implementation(project(":core:database"))
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(project(":design-system"))
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
            implementation(libs.koin.test)
            implementation(project(":core:database-test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(project(":common-test"))
            implementation(libs.assertk.common)
        }
    }
}
