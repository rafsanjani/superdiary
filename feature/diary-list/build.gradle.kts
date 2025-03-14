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
            implementation(libs.google.testparameterinjector)
            implementation(projects.commonTest)
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(projects.core.logging)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.compose)
            implementation(projects.commonUtils)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlin.datetime)
            implementation(projects.core.authentication)
            implementation(projects.commonUtils)
            implementation(libs.richTextEditor)
            implementation(projects.core.location)
            implementation(projects.swipe)
            implementation(projects.sharedData)
            implementation(projects.core.database)
            implementation("org.mobilenativefoundation.store:store5:5.1.0-alpha06")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
            implementation(projects.designSystem)
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

android {
    namespace = "com.foreverrafs.superdiary.diarylist"
}
