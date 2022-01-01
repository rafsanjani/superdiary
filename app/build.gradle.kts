val kotlin_version: String by extra
plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs")
    kotlin("android")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.parcelize")
}
apply {
    plugin("kotlin-android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.foreverrafs.superdiary.HiltTestRunner"
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

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-rc01"
    }


    packagingOptions {
        resources.excludes.add("META-INF/notice.txt")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        this.jvmTarget = "1.8"
    }
}


dependencies {
    // compose dependencies
    implementation(AndroidX.activity.compose)
    // Compose Material Design
    implementation(AndroidX.compose.material)
    // Animations
    implementation(AndroidX.compose.animation)
    // Tooling support (Previews, etc.)
    implementation(AndroidX.compose.ui.tooling)
    // UI Tests
    androidTestImplementation(AndroidX.compose.ui.testJunit4)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.activity.ktx)
    kapt(AndroidX.hilt.compiler)
    implementation(AndroidX.fragment.ktx)
    implementation(AndroidX.room.ktx)
    implementation(Google.dagger.hilt.android)
    androidTestImplementation(Google.dagger.hilt.android.testing)
    kapt(Google.dagger.hilt.android.compiler)
    kapt(AndroidX.room.compiler)
    implementation(Google.firebase.bom)
    implementation(AndroidX.navigation.ui)
    implementation(AndroidX.navigation.fragmentKtx)
    implementation(AndroidX.navigation.commonKtx)
    implementation(AndroidX.fragment.testing)
    implementation(Kotlin.stdlib.jdk8)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.core.animation)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.preference.ktx)
    testImplementation(AndroidX.test.ext.junitKtx)
    testImplementation(CashApp.turbine)
    testImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.ext.junitKtx)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.test.espresso.contrib)
    androidTestImplementation(AndroidX.test.coreKtx)
    androidTestImplementation(AndroidX.test.rules)
    testImplementation(AndroidX.test.ext.truth)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(Square.moshi.kotlinCodegen)
    kaptAndroidTest(Google.dagger.hilt.android.compiler)
    testImplementation(Square.moshi.kotlinReflect)

    implementation(AndroidX.dataStore.core)
    implementation(AndroidX.dataStore.preferences)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("com.github.Rafsanjani:datepickertimeline:0.5.0")
}