@file:Suppress("UnusedPrivateProperty")

plugins {
    kotlin("android")
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("io.sentry.android.gradle") version "4.14.1"
}

android {
    compileSdk = 35
    defaultConfig {
        applicationId = "com.foreverrafs.superdiary"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val sentryBaseUrl = System.getenv("SENTRY_BASE_URL_ANDROID") ?: ""
        if (sentryBaseUrl.isEmpty()) {
            logger.warn(
                "Sentry base url hasn't been set. Please add SENTRY_BASE_URL_ANDROID to your environment variables",
            )
        }

        manifestPlaceholders["sentryBaseUrl"] = sentryBaseUrl
        manifestPlaceholders["applicationName"] = "superdiary"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
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
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
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
            "WARN:Signing parameters not found. You can't build release variants."
        )
    }
}

sentry {
    val sentryToken = System.getenv("SENTRY_AUTH_TOKEN") ?: ""

    if (sentryToken.isEmpty()) {
        logger.warn(
            "Sentry token hasn't been set. Please add SENTRY_AUTH_TOKEN to your environment variables",
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
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"

    // These values from secrets.properties are used in :core:secrets module to generate runtime secrets.
    ignoreList.add("OPENAI_KEY")
    ignoreList.add("GOOGLE_SERVER_CLIENT_ID")
}

dependencies {
    implementation(libs.richTextEditor)
    implementation(libs.moko.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlin.datetime)
    implementation(libs.google.material)
    implementation(compose.runtime)
    implementation(projects.sharedUi)
    implementation(projects.core.logging)
    implementation(projects.sharedData)
    implementation(projects.core.analytics)
    implementation(libs.koin.android)
    implementation(projects.core.utils)
    testImplementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    kotlin("android")
}

dependencies {
    implementation(libs.richTextEditor)
    implementation(libs.moko.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlin.datetime)
    implementation(libs.google.material)
    implementation(compose.runtime)
    implementation(projects.sharedUi)
    implementation(projects.core.logging)
    implementation(projects.sharedData)
    implementation(projects.core.analytics)
    implementation(libs.koin.android)
    implementation(projects.core.utils)
    testImplementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}
