package com.superdiary.gradle.multiplatform

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }

        with(target) {
            composeCompiler {
                reportsDestination.set(target.layout.buildDirectory.dir("compose_compiler"))
                metricsDestination.set(target.layout.buildDirectory.dir("compose_compiler"))
            }
        }
    }
}

fun Project.composeCompiler(action: ComposeCompilerGradlePluginExtension.() -> Unit) {
    extensions.configure<ComposeCompilerGradlePluginExtension>(action)
}
