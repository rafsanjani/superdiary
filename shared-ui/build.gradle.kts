@file:Suppress("UnusedPrivateProperty")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.paparazzi)
    kotlin("multiplatform")
    id("kotlin-parcelize")
    alias(libs.plugins.mokkery)
}

kotlin.sourceSets.all {
    languageSettings.enableLanguageFeature("ExplicitBackingFields")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget()

    jvm()
    jvmToolchain(17)

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            linkerOpts += "-lsqlite3"
            export(projects.core.analytics)
            export(projects.core.logging)
            export(projects.core.location)
            export(projects.core.auth)
        }
    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("DataObjects")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation(projects.sharedData)
                implementation(projects.core.utils)
                implementation(libs.kotlin.datetime)
                implementation(libs.koin.core)
                implementation(libs.touchlab.kermit)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.koin.compose)
                implementation(projects.swipe)
                implementation(libs.uuid)
                implementation(libs.richTextEditor)
                implementation(libs.touchlab.stately)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.androidx.datastore.core)
                implementation(libs.jetbrains.navigation.compose)
                implementation(libs.kotlinx.serialization.json)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.coil3.compose)
                implementation(libs.coil3.compose.core)
                implementation(libs.coil3.network.ktor)
                implementation(libs.coil3.multiplatform)
                implementation(libs.ktor.client.core)

                api(projects.core.auth)
                api(projects.core.analytics)
                api(projects.core.location)
                api(projects.core.logging)
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
                implementation("com.google.maps.android:maps-compose:6.2.0")
//                implementation(libs.ktor.client.okhttp)
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

    sourceSets["main"].res.srcDirs("src/commonMain/resources")
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
    testImplementation(project(":shared-data"))
}
