@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)

                // project dependency
                implementation(projects.core.sync)
                implementation(projects.designSystem)

                implementation(libs.koin.compose.viewmodel)

                implementation(compose.materialIconsExtended)

                // feature modules
                implementation(projects.feature.diaryFavorite)
                implementation(projects.feature.diaryChat)
                implementation(projects.feature.createDiary)
                implementation(projects.feature.diaryProfile)
                implementation(projects.feature.diaryAuth)
                implementation(projects.feature.diaryDashboard)
                implementation(projects.feature.diaryList)

                implementation(projects.core.authentication)
                implementation(projects.core.logging)
                implementation(projects.core.diaryAi)
                implementation(projects.core.analytics)
                implementation(projects.core.permission)
                implementation(projects.commonUtils)

                implementation(projects.sharedData)
                implementation(libs.coil3.compose.core)
                implementation(libs.coil3.compose)
                implementation(libs.coil3.multiplatform)
                implementation(libs.coil3.network.ktor)

                implementation(libs.jetbrains.navigation.compose)
            }
        }

        commonTest {
            dependencies {
            }

            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }

        androidUnitTest {
            dependencies {
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.koin.jvm)
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.navigation"
}
