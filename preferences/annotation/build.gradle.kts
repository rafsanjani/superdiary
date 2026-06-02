plugins {
    alias(conventionLibs.plugins.superdiary.multiplatform.kotlin)
    alias(conventionLibs.plugins.superdiary.android.library)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()
}
