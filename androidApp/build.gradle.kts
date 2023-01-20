plugins {
    id("com.android.application")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6" // Depends on your kotlin version
    kotlin("android")
}

android {
    namespace = "com.foreverrafs.superdiary.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary.android"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.1"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(platform(libs.compose.bom))
    ksp(libs.compose.destinations.processor)
    implementation(libs.compose.destinations.runtime)
    implementation(libs.compose.ui.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling.data)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.foundation.foundation)
    implementation(libs.compose.material.material)
    implementation(libs.compose.material.iconsextended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.datePickerTimeline)
}