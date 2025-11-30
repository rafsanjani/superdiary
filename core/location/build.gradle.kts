import com.superdiary.gradle.multiplatform.applyAllMultiplatformTargets

plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    applyAllMultiplatformTargets()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
