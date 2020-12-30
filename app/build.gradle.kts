plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs")
    kotlin("android")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary"
        minSdkVersion(23)
        compileSdkVersion(30)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    viewBinding {
        android.buildFeatures.viewBinding = true
    }

    packagingOptions {
        exclude("META-INF/notice.txt")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }



    this.kotlinOptions {
        this.jvmTarget = "1.8"
    }
}


dependencies {
    implementation(AndroidX.appCompat)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.activityKtx)
    implementation(AndroidX.hilt.lifecycleViewModel)
    implementation(project(mapOf("path" to ":diarycalendar")))
    kapt(AndroidX.hilt.compiler)
    implementation(AndroidX.fragmentKtx)
    implementation(AndroidX.room.ktx)
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)
    kapt(AndroidX.room.compiler)
    implementation(AndroidX.navigation.ui)
    implementation(AndroidX.navigation.fragmentKtx)
    implementation(AndroidX.navigation.commonKtx)
    implementation(AndroidX.fragmentTesting)
    implementation(Kotlin.stdlib.jdk8)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModel)
    implementation(AndroidX.core.animation)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.preferenceKtx)
    testImplementation(AndroidX.test.ext.junitKtx)
    testImplementation(CashApp.turbine)
    testImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.ext.junitKtx)
    testImplementation(AndroidX.test.ext.truth)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(Square.moshi.kotlinCodegen)
    testImplementation("io.mockk:mockk:1.10.4")
    testImplementation(Square.moshi.kotlinReflect)

    implementation("androidx.datastore:datastore-core:_")
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha05")
    implementation("jp.wasabeef:recyclerview-animators:4.0.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.1")
}