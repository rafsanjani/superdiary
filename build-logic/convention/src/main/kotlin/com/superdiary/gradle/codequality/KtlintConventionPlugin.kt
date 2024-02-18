package com.superdiary.gradle.codequality

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            subprojects {
                pluginManager.apply("org.jlleitschuh.gradle.ktlint")

                ktlint {
                    filter {
                        exclude { it.file.path.contains("${layout.buildDirectory.get()}") }
                    }
                }
            }
        }
    }
}

private fun Project.ktlint(action: KtlintExtension.() -> Unit) = extensions.configure<KtlintExtension>(action)
