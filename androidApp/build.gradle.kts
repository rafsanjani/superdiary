import org.jetbrains.kotlin.container.composeContainer

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.application)
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.appcompat)
                implementation(libs.material)
                implementation(libs.androidx.activity.ktx)
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.uiTooling)
                implementation(compose.material3)
                implementation(projects.shared)
                implementation(projects.data)
            }
        }
    }
}

android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    compileSdk = 34
    defaultConfig {
        applicationId = "com.foreverrafs.gamehub"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "com.foreverrafs.gamehub"

    packaging {
        resources.excludes.add(
            "META-INF/versions/9/previous-compilation-data.bin",
        )
    }
}
