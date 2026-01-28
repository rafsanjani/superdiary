plugins {
    id("com.superdiary.multiplatform.compose")
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
            freeCompilerArgs += "-Xbinary=bundleId=com.foreverrafs.shared"

            export(project(":core:analytics"))
            export(project(":core:logging"))
            export(project(":core:location"))
            export(project(":core:authentication"))
            export(project(":design-system"))
            export(project(":navigation"))
            export(project(":core:diary-ai"))
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":core:analytics"))
                api(project(":core:logging"))
                api(project(":core:location"))
                api(project(":navigation"))
                api(project(":core:authentication"))
                api(project(":design-system"))
                api(libs.kotlinx.serialization.json)
                api(project(":core:diary-ai"))
            }
        }
    }
}
