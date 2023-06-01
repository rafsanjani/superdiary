@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    kotlinOptions {
        val experimentalOptIns = listOf(
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-Xopt-in=androidx.compose.ui.graphics.ExperimentalGraphicsApi",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
        )

        freeCompilerArgs =
            freeCompilerArgs + experimentalOptIns

        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(projects.data)
    implementation(platform(libs.compose.bom))
    implementation(projects.calendar)
    implementation(libs.compose.destinations.runtime)
    ksp(libs.compose.destinations.processor)
    implementation(libs.compose.ui.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.accompanist.permissions)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling.data)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.foundation.foundation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.navigation)
    implementation(libs.compose.material.material3)
    implementation(libs.compose.material.material3.windowSizeClass)
    implementation(libs.compose.material.iconsextended)
    implementation(libs.androidx.material)
    ksp(libs.kotlin.inject.compiler.ksp)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.systemUiController)
    implementation(libs.datePickerTimeline)
    implementation(libs.kotlin.datetime)
}
