@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.foreverrafs.superdiary.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary.android"
        minSdk = 28
        targetSdk = 34
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
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
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
    implementation(projects.shared)
    implementation(projects.data)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.compose.foundation.foundation)
    implementation(libs.compose.material.material3)
    implementation(libs.androidx.material)
    implementation(libs.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
}
