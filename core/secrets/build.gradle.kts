@file:Suppress("UnusedPrivateProperty")

import com.superdiary.gradle.multiplatform.applyAllMultiplatformTargets


plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    id("com.superdiary.secrets")
}

kotlin {
    applyAllMultiplatformTargets()
}
