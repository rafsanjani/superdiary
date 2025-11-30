plugins {
    id("com.superdiary.multiplatform.kotlin")
}

android {
    namespace = "com.foreverrafs.preferences"
    compileSdk = libs.versions.compileSdk.get().toInt()
}
