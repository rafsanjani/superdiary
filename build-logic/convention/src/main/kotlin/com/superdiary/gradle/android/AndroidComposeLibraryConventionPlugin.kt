package com.superdiary.gradle.android

import com.android.build.api.dsl.LibraryBuildFeatures
import com.android.build.api.dsl.LibraryExtension
import com.superdiary.gradle.findVersion
import com.superdiary.gradle.kotlin.configureKotlin
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
        }
    }
}

private fun Project.android(action: LibraryExtension.() -> Unit) =
    extensions.configure<LibraryExtension>(action)

private fun Project.buildFeatures(action: LibraryBuildFeatures.() -> Unit) =
    extensions.configure<LibraryBuildFeatures>(action)
