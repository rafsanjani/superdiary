@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.logging)
            implementation(projects.core.diaryAi)
            implementation(projects.designSystem)
            implementation(projects.sharedData)
            implementation(projects.commonUtils)

            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.compose.viewmodel)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.assertk.common)
            implementation(libs.junit)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.commonTest)
        }
    }
}
