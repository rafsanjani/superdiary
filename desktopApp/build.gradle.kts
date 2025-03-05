import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(libs.koin.jvm)
                implementation(projects.core.analytics)
                implementation(projects.core.logging)
                implementation(projects.core.database)
                implementation(projects.sharedUi)
                implementation(projects.sharedData)
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xskip-prerelease-check",
        )
    }
}

compose.desktop {
    application {
        mainClass = "com.foreverrafs.superdiary.ApplicationKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Msi,
                TargetFormat.Deb,
            )
            packageName = "Superdiary"
            packageVersion = "1.0.0"
        }
    }
}
