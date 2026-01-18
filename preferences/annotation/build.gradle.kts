plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()
}
