@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(libs.richTextEditor)
                implementation(libs.touchlab.stately)
                implementation(compose.components.uiToolingPreview)
                api(libs.jetbrains.compose.ui.backhandler)
                api("org.jetbrains.compose.material3:material3:1.10.0-alpha04+dev3166")
                implementation(libs.coil3.compose)
                implementation(libs.coil3.compose.core)
                implementation(libs.jetbrains.navigation.compose)
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
                implementation(libs.koin.android)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockk)
                implementation(libs.google.testparameterinjector)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.client.cio)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.compose.ui.tooling)
                implementation(libs.androidx.activity.compose)
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
}
