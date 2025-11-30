package com.superdiary.gradle.multiplatform

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.library")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            applyDefaultHierarchyTemplate()
            jvm()

            if (pluginManager.hasPlugin("com.android.library")) {
                androidTarget()
            }

            iosArm64()
            iosSimulatorArm64()

            targets.withType<KotlinNativeTarget>().configureEach {
                binaries.configureEach {
                    linkerOpts("-lsqlite3")
                }
            }

            targets.configureEach {
                compilations.configureEach {
                    compileTaskProvider.configure {
                        compilerOptions {
                            freeCompilerArgs.add("-Xexpect-actual-classes")
                            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
                            freeCompilerArgs.add("-Xcontext-parameters")
                        }
                    }
                }
            }
        }
    }
}
