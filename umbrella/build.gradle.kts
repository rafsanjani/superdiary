@file:Suppress("UnusedPrivateProperty")
@file:OptIn(ExperimentalSwiftExportDsl::class)

import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl


plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
}
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
            export(projects.navigation)
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.analytics)
                api(projects.core.logging)
                api(projects.core.location)
                api(projects.navigation)
                api(projects.core.authentication)
                api(projects.designSystem)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.superdiary.umbrella"
}
