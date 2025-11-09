@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.paparazzi)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        androidUnitTest.dependencies {
        }

        commonMain.dependencies {
            implementation(projects.core.logging)
            implementation(projects.core.diaryAi)
            implementation(projects.designSystem)
            implementation(projects.core.database)
            implementation(projects.sharedData)

            // replace this with a catalog entry
            implementation(compose.material3)

            // compose dependencies
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui.backhandler)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.jetbrains.compose.resources)

            // coroutines
            implementation(libs.kotlinx.coroutines.core)

            // koin
            implementation(libs.koin.compose.viewmodel)

            // kotlinx-serialization
            implementation(libs.kotlinx.serialization.json)

            // rich text
            implementation(libs.richTextEditor)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.chat"
}
