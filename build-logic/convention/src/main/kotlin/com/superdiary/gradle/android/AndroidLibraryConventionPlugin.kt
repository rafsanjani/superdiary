package com.superdiary.gradle.android

import com.android.build.api.dsl.LibraryExtension
import com.superdiary.gradle.findVersion
import com.superdiary.gradle.kotlin.configureKotlin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            android {
                compileSdk = findVersion("compileSdk").toInt()

                defaultConfig {
                    minSdk = findVersion("minimumSdk").toInt()
                }

                compileOptions {
                    targetCompatibility = JavaVersion.VERSION_17
                    sourceCompatibility = JavaVersion.VERSION_17
                }
            }

            configureKotlin()
        }
    }
}

private fun Project.android(action: LibraryExtension.() -> Unit) =
    extensions.configure<LibraryExtension>(action)
