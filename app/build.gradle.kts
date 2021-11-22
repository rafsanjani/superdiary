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
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary"
        minSdkVersion(23)
        compileSdkVersion(30)
        targetSdkVersion(30)
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


    buildFeatures.viewBinding = true


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
    implementation(project(mapOf("path" to ":diarycalendar")))

    implementation(AndroidX.appCompat)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.activityKtx)
    implementation(AndroidX.hilt.lifecycleViewModel)
    kapt(AndroidX.hilt.compiler)
    implementation(AndroidX.fragmentKtx)
    implementation(AndroidX.room.ktx)
    implementation(Google.dagger.hilt.android)
    androidTestImplementation(Google.dagger.hilt.android.testing)
    kapt(Google.dagger.hilt.android.compiler)
    kapt(AndroidX.room.compiler)
    implementation(Google.firebase.bom)
//    implementation(Google.firebase.analytics)
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
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.test.espresso.contrib)
    androidTestImplementation(AndroidX.test.coreKtx)
    androidTestImplementation(AndroidX.test.rules)
    testImplementation(AndroidX.test.ext.truth)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(Square.moshi.kotlinCodegen)
    kaptAndroidTest(Google.dagger.hilt.android.compiler)
    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation(Square.moshi.kotlinReflect)

    implementation("androidx.datastore:datastore-core:_")
    implementation("androidx.datastore:datastore-preferences:_")
    implementation("jp.wasabeef:recyclerview-animators:4.0.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
}