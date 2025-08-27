plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    androidTarget()
}

android {
    namespace = "com.foreverrafs.preferences"
    compileSdk = libs.versions.compileSdk.get().toInt()
}
