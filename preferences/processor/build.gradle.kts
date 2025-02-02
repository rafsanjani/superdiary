plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.preferences.annotation)
            implementation(libs.square.kotlinPoet)
            implementation("com.squareup:kotlinpoet-ksp:2.0.0")
            implementation("com.google.devtools.ksp:symbol-processing-api:2.1.10-1.0.29")
        }
    }
}

android {
    namespace = "com.foreverrafs.preferences.processor"
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
