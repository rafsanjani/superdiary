import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            // Configures a JavaExec task named "runJvm" and a Gradle distribution for the "main" compilation in this target
            executable {
                mainClass.set("com.foreverrafs.superdiary.ApplicationKt")
            }

            // Configures a JavaExec task named "runJvmTest" and a Gradle distribution for the "test" compilation
            executable(KotlinCompilation.TEST_COMPILATION_NAME) {
                mainClass.set("com.foreverrafs.superdiary.ApplicationKt")
            }
        }
    }
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
