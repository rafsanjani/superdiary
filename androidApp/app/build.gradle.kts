@file:Suppress("UnusedPrivateProperty")

import com.google.firebase.appdistribution.gradle.firebaseAppDistributionDefault
import io.sentry.android.gradle.extensions.InstrumentationFeature
import org.jetbrains.kotlin.konan.properties.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sentry)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.dependencyguard)
}

val properties = Properties().apply {
    val file = rootProject.file("secrets.properties")

    if (file.exists()) {
        load(file.inputStream())
    } else {
        load(rootProject.file("secrets.defaults.properties").inputStream())
    }
}

android {
    defaultConfig {
        compileSdk = libs.versions.compileSdk.get().toInt()

        applicationId = "com.foreverrafs.superdiary"
        minSdk = libs.versions.minimumSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = getGitCommitCount()
        versionName = "0.0.1"

        val sentryBaseUrl = System.getenv("SENTRY_BASE_URL_ANDROID") ?: ""
        if (sentryBaseUrl.isEmpty()) {
            logger.warn(
                "Sentry base url hasn't been set. Please add SENTRY_BASE_URL_ANDROID to your environment variables",
            )
        }

        resValue("string", "google_maps_key", properties.getProperty("MAPS_API_KEY"))
        manifestPlaceholders["sentryBaseUrl"] = sentryBaseUrl
        manifestPlaceholders["applicationName"] = "superdiary"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    namespace = "com.foreverrafs.superdiary.app"

    // This will be populated in CI by configureReleaseSigning()
    signingConfigs.create("release")

    buildTypes {
        release {
            configureReleaseSigning()
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFile("proguard-rules.pro")

            manifestPlaceholders["sentryEnvironment"] = "production"
            manifestPlaceholders["applicationName"] = "superdiary"

            val applicationId = properties.getProperty("FIREBASE_DISTRIBUTION_APP_ID")
            if (applicationId.isNullOrBlank()) {
                logger.warn("FIREBASE_DISTRIBUTION_APP_ID is not set or is empty in secrets.properties. This is required for Firebase App Distribution.")
            }

            firebaseAppDistributionDefault {
                appId = applicationId
                artifactType = "APK"
                groups = "default"
                serviceCredentialsFile = "firebase_credentials.json"
            }
        }

        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["sentryEnvironment"] = "debug"
            manifestPlaceholders["applicationName"] = "superdiary debug"
        }

        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")

            manifestPlaceholders["sentryEnvironment"] = "benchmark"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    flavorDimensions.add("mode")

    productFlavors {
        create("standard") {
            dimension = "mode"
            isDefault = true
        }

        create("demo") {
            applicationIdSuffix = ".demo"
            manifestPlaceholders["applicationName"] = "superdiary demo"
            dimension = "mode"
        }
    }

    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
}

fun Project.getGitCommitCount(): Int = try {
    val result = providers.exec {
        commandLine("git", "rev-list", "--count", "HEAD")
    }.standardOutput.asText.get()

    result.trim().toInt()
} catch (e: Exception) {
    throw e
}

fun configureReleaseSigning() {
    val storeFile = findProperty("STORE_FILE")?.toString()
    val keyAlias = findProperty("KEY_ALIAS")?.toString()
    val storePassword = findProperty("STORE_PASSWORD")?.toString()
    val keyPassword = findProperty("KEY_PASSWORD")?.toString()

    if (storeFile != null && keyAlias != null && storePassword != null && keyPassword != null) {
        android.signingConfigs.getByName("release") {
            this.storeFile = File("${rootProject.projectDir.path}//$storeFile")
            this.storePassword = storePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
        }
        logger.info("Signing parameters injected")
    } else {
        logger.warn(
            "WARN:Signing parameters not found. You can't build release variants.",
        )
    }
}

sentry {
    val sentryToken = System.getenv("SENTRY_AUTH_TOKEN") ?: ""

    if (sentryToken.isEmpty()) {
        logger.warn(
            "WARN:Sentry token hasn't been set. Please add SENTRY_AUTH_TOKEN to your environment variables",
        )
    }

    org.set("rafsanjani-inc")
    projectName.set("superdiary-android")
    authToken.set(sentryToken)

    // this will upload your source code to Sentry to show it as part of the stack traces
    // disable if you don't want to expose your sources
    includeSourceContext.set(true)
    autoUploadProguardMapping.set(true)
    uploadNativeSymbols.set(true)

    tracingInstrumentation {
        enabled = true
        features = setOf(
            InstrumentationFeature.DATABASE,
            InstrumentationFeature.FILE_IO,
            InstrumentationFeature.OKHTTP,
            InstrumentationFeature.COMPOSE,
        )
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.google.material)
    implementation(platform("io.sentry:sentry-bom:8.32.0"))
    implementation(projects.navigation)
    implementation(projects.sharedData)
    implementation(projects.core.diaryAi)
    implementation(projects.feature.diaryAuth)
    implementation(projects.core.analytics)
    implementation(libs.koin.android)
    implementation(libs.supabase.compose.auth)
    implementation(libs.androidx.core.uri)
    implementation(libs.supabase.auth)
    implementation(libs.koin.core)
    implementation(projects.core.authentication)
    implementation(projects.core.logging)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.coroutines.test)
}

dependencyGuard {
    configuration("standardReleaseRuntimeClasspath")
}
