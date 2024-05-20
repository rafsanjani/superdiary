@file:Suppress("UnusedPrivateProperty")

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.paparazzi)
    id("org.jetbrains.kotlinx.kover")
}

koverReport {
    filters {
        excludes {
            // Exclude everything in the android app
            packages(
                "com.foreverrafs",
            )
        }
    }
}

kotlin {
    androidTarget()

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.richTextEditor)
                implementation(libs.androidx.activity.compose)
                implementation(libs.kotlin.datetime)
                implementation(libs.google.material)
                implementation(projects.sharedUi)
                implementation(compose.material3)
                implementation(compose.uiTooling)
                implementation(libs.compose.ui.tooling.preview)
                implementation(projects.sharedData)
                implementation(projects.core.analytics)
                implementation(libs.koin.android)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.cashapp.paparazzi)
                implementation(libs.koin.android)
                implementation(libs.koin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.mockk)
                implementation(libs.google.testparameterinjector)
            }
        }
    }
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions{
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    namespace = "com.foreverrafs.superdiary.app"

    sourceSets["main"].res.srcDirs(
        rootProject.projectDir.path + "shared-ui/src/commonMain/composeResources",
    )

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            proguardFile("proguard-rules.pro")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
}
