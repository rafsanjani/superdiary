@file:Suppress("UnusedPrivateProperty")

import com.superdiary.gradle.codequality.koverReport
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi


plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.paparazzi)
    id("io.sentry.android.gradle") version "4.8.0"
    id("org.jetbrains.kotlinx.kover")
}

koverReport {
    filters {
        excludes {
            // Exclude everything in the android app
            packages(
                "com.foreverrafs",
            )
        }
    }
}

kotlin {
    androidTarget()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xskip-prerelease-check",
        )
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.richTextEditor)
                implementation(libs.androidx.activity.compose)
                implementation(libs.kotlin.datetime)
                implementation(libs.google.material)
                implementation(projects.sharedUi)
                implementation(projects.core.logging)
                implementation(compose.material3)
                implementation(compose.uiTooling)
                implementation(libs.compose.ui.tooling.preview)
                implementation(projects.sharedData)
                implementation(projects.core.analytics)
                implementation(libs.koin.android)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.cashapp.paparazzi)
                implementation(libs.koin.android)
                implementation(libs.koin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.mockk)
                implementation(libs.google.testparameterinjector)
            }
        }
    }
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    namespace = "com.foreverrafs.superdiary.app"

    sourceSets["main"].res.srcDirs(
        rootProject.projectDir.path + "shared-ui/src/commonMain/composeResources",
    )

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            proguardFile("proguard-rules.pro")

            val sentryBaseUrl = System.getenv("SENTRY_BASE_URL_RELEASE")
                ?: throw IllegalArgumentException(
                    "Sentry base url hasn't been set. Please add SENTRY_BASE_URL_RELEASE to your environment variables",
                )

            manifestPlaceholders["sentryBaseUrl"] = sentryBaseUrl
            manifestPlaceholders["sentryEnvironment"] = "production"
        }

        debug {
            val sentryBaseUrl = System.getenv("SENTRY_BASE_URL_DEBUG")
                ?: throw IllegalArgumentException(
                    "Sentry base url hasn't been set. Please add SENTRY_BASE_URL_DEBUG to your environment variables",
                )

            manifestPlaceholders["sentryBaseUrl"] = sentryBaseUrl
            manifestPlaceholders["sentryEnvironment"] = "debug"
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
}

tasks.getByName("generateResourceAccessorsForAndroidMain")
    .dependsOn(
        "sentryCollectSourcesRelease",
        "generateSentryBundleIdRelease",
        "generateSentryBundleIdDebug",
        "generateSentryBundleIdBenchmark",
    )

sentry {
    val sentryToken = System.getenv("SENTRY_AUTH_TOKEN")
        ?: throw IllegalArgumentException(
            "Sentry token hasn't been set. Please add SENTRY_AUTH_TOKEN to your environment variables",
        )

    org.set("rafsanjani-inc")
    projectName.set("superdiary-debug")
    authToken.set(sentryToken)

    // this will upload your source code to Sentry to show it as part of the stack traces
    // disable if you don't want to expose your sources
    includeSourceContext.set(true)
    autoUploadProguardMapping.set(true)
    uploadNativeSymbols.set(true)
}
