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
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0-SNAPSHOT+release-1-10")
                implementation(libs.jetbrains.lifecycle.runtime.compose)
                implementation(libs.androidx.core.uri)

                // project dependency
                implementation(projects.core.database)
                implementation(projects.designSystem)

                implementation(libs.koin.compose.viewmodel)

                // TODO: Remove this dependency
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
                implementation(libs.jetbrains.compose.navigation3)
            }
        }

        commonTest {
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }

        androidUnitTest {
            dependencies {}
        }

        jvmMain {
            dependencies {
                implementation(libs.koin.jvm)
            }
        }
    }
}
