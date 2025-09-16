plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.foreverrafs.superdiary.benchmark"
    compileSdk = libs.versions.targetSdk.get().toInt()

    kotlin {
        jvmToolchain(21)
    }

    defaultConfig {
        minSdk = 28
        targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testInstrumentationRunnerArguments += mapOf(
            "androidx.benchmark.suppressErrors" to "EMULATOR",
        )
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("demoRelease")
        }
    }

    flavorDimensions.add("mode")

    productFlavors {
        create("demo") { dimension = "mode" }
        create("standard") { dimension = "mode" }
    }

    targetProjectPath = ":androidApp:app"
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.espressoCore)
    implementation(libs.androidx.test.uiAutomator)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.benchmark.macro)
}
