@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.mokkery)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            linkerOpts += "-lsqlite3"
            linkerOpts += "-ld_classic"

            export(projects.core.analytics)
            export(projects.core.logging)
            export(projects.core.location)
            export(projects.core.authentication)
            export(projects.designSystem)
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation(projects.sharedData)
                implementation(projects.commonUtils)
                implementation("org.jetbrains.androidx.navigation3:navigation3-ui:1.0.0-SNAPSHOT+update-templates-demos")
                implementation(libs.kotlin.datetime)
                implementation(libs.androidx.core.uri)
                implementation(libs.koin.core)
                implementation(libs.touchlab.kermit)
                implementation(libs.koin.compose)
                implementation(libs.uuid)
                implementation(libs.richTextEditor)
                implementation(libs.touchlab.stately)
                implementation(libs.jetbrains.navigation.compose)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.coil3.compose)
                implementation(libs.coil3.compose.core)
                implementation(libs.coil3.network.ktor)
                implementation(libs.coil3.multiplatform)
                implementation(libs.ktor.client.core)
                implementation(libs.jetbrains.lifecycle.viewmodel)
                implementation(libs.jetbrains.lifecycle.runtime.compose)
                implementation("com.valentinilk.shimmer:compose-shimmer:1.3.3")
                implementation(libs.koin.compose.viewmodel)
                api(projects.core.authentication)
                api(projects.core.analytics)
                api(projects.core.location)
                api(projects.core.logging)
                api(projects.designSystem)
                api(projects.core.permission)
                implementation(projects.core.diaryAi)
                implementation(projects.feature.diaryProfile)
                implementation(projects.feature.diaryAuth)
                implementation(projects.feature.diaryList)
                implementation(projects.feature.diaryDashboard)
                implementation(projects.feature.createDiary)
                implementation(projects.feature.diaryFavorite)
                implementation(projects.core.sync)
                implementation(projects.core.permission)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk.common)
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(
                    compose.uiTest,
                )
                implementation(libs.turbine)
                implementation(projects.commonTest)
            }

            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }

        androidUnitTest {
            dependencies {
                implementation(libs.cashapp.paparazzi)
                implementation(libs.koin.android)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockk)
                implementation(projects.commonTest)
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
    namespace = "com.foreverrafs.superdiary.shared"
}

afterEvaluate {
    tasks.named("iosSimulatorArm64ResolveResourcesFromDependencies") {
        doFirst {
            rootProject.subprojects.forEach {
                delete(it.layout.buildDirectory.file("kover/kover.artifact"))
            }
        }
    }
}

tasks.named("iosSimulatorArm64ResolveResourcesFromDependencies") {
    doFirst {
        rootProject.subprojects.forEach {
            delete(it.layout.buildDirectory.file("kover/kover.artifact"))
        }
    }
}

tasks.named("iosArm64ResolveResourcesFromDependencies") {
    doFirst {
        rootProject.subprojects.forEach {
            delete(it.layout.buildDirectory.file("kover/kover.artifact"))
        }
    }
}

dependencies {
    testImplementation(projects.sharedData)
}
