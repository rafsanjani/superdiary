package com.superdiary.gradle.codequality

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlinx.kover")

            kover {
                useJacoco("0.8.10")

                reports {
                    filters {
                        excludes {
                            packages(KoverExclusionList.packages)
                            classes(KoverExclusionList.classes)
                            files(KoverExclusionList.files)
                        }
                    }
                }
            }
        }
    }
}

private fun Project.kover(action: KoverProjectExtension.() -> Unit) =
    extensions.configure<KoverProjectExtension>(
        action,
    )
