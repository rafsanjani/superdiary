@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.android.library")
    id("com.superdiary.multiplatform.kotlin")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(projects.core.logging)
                implementation(libs.kotlinx.coroutines.test)
                implementation("org.simpmusic.gemini-kotlin:openai-client:4.0.2")
                implementation(libs.kotlin.datetime)
                implementation(projects.commonUtils)
                implementation(projects.sharedData)
                implementation(projects.core.database)
                implementation(projects.core.secrets)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(projects.core.databaseTest)
                implementation(libs.assertk.common)
            }
        }

        androidMain {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp-jvm:3.3.3")
            }
        }

        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp-jvm:3.3.3")
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.ai"
}
