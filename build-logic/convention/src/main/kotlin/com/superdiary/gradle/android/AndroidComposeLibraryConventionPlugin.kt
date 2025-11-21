package com.superdiary.gradle.android

import com.android.build.api.dsl.LibraryExtension
import com.superdiary.gradle.findVersion
import com.superdiary.gradle.kotlin.configureKotlin
import com.superdiary.gradle.multiplatform.composeCompiler
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            android {
                compileSdk = findVersion("compileSdk").toInt()

                defaultConfig {
                    minSdk = findVersion("minimumSdk").toInt()
                }
            }

            configureKotlin()
            composeCompiler {
                reportsDestination.set(target.layout.buildDirectory.dir("compose_compiler"))
                metricsDestination.set(target.layout.buildDirectory.dir("compose_compiler"))
            }
        }
    }
}

private fun Project.android(action: LibraryExtension.() -> Unit) =
    extensions.configure<LibraryExtension>(action)
