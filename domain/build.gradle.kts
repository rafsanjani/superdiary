plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    testImplementation(AndroidX.test.ext.junitKtx)
    testImplementation(CashApp.turbine)
    testImplementation(AndroidX.archCore.testing)
    implementation(Kotlin.stdlib.jdk8)
    testImplementation(AndroidX.test.ext.truth)
    testImplementation(KotlinX.coroutines.test)
    implementation(KotlinX.coroutines.core)
    testImplementation(Square.moshi.kotlinCodegen)
    testImplementation(Square.moshi.kotlinReflect)
}