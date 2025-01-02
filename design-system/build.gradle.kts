@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget()

    jvm()
    jvmToolchain(17)

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation(libs.richTextEditor)
                implementation(libs.touchlab.stately)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.coil3.compose)
                implementation(libs.coil3.compose.core)
                implementation(libs.coil3.network.ktor)
                implementation(libs.coil3.multiplatform)
                implementation(libs.ktor.client.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk.common)
                implementation(libs.junit)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.cashapp.paparazzi)
                implementation(libs.koin.android)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockk)
                implementation(libs.google.testparameterinjector)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.koin.jvm)
                implementation(libs.ktor.client.cio)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.compose.ui.tooling)
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.google.maps.compose)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.moko.permissions)
                implementation(libs.ktor.client.darwin)
                implementation(libs.moko.permissions.compose)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.design"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
