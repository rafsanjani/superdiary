plugins {
    id("com.superdiary.android.library")
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
            implementation("com.squareup:kotlinpoet-ksp:2.2.0")
            implementation("com.google.devtools.ksp:symbol-processing-api:2.2.20-2.0.3")
        }
    }
}

android {
    namespace = "com.foreverrafs.preferences.processor"
}
