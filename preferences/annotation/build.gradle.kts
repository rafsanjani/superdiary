plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    id("com.superdiary.multiplatform.kotlin")
}

android {
    namespace = "com.foreverrafs.preferences"
    compileSdk = libs.versions.compileSdk.get().toInt()
}
