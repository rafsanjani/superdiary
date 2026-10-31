plugins {
    alias(conventionLibs.plugins.superdiary.multiplatform.compose)
    alias(conventionLibs.plugins.superdiary.multiplatform.kotlin)
    alias(conventionLibs.plugins.superdiary.android.library)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            linkerOpts += "-lsqlite3"
            freeCompilerArgs += "-Xbinary=bundleId=com.foreverrafs.shared"

            export(projects.core.analytics)
            export(projects.core.logging)
            export(projects.core.location)
            export(projects.core.authentication)
            export(projects.designSystem)
            export(projects.navigation)
            export(projects.core.diaryAi)
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
                api(libs.kotlinx.serialization.json)
                api(projects.core.diaryAi)
            }
        }
    }
}
