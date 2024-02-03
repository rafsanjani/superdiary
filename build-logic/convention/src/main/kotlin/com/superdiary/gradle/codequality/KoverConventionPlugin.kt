package com.superdiary.gradle.codequality

import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlinx.kover")

            koverReport {
                filters {
                    excludes {
                        packages(
                            "com.foreverrafs.superdiary.database",
                            "db",
                            "com.foreverrafs.superdiary.ui",
                        )
                        files("BottomNavigationScreenKt")
                    }
                }
            }
        }
    }
}

private fun Project.koverReport(
    action: KoverReportExtension.() -> Unit
) = extensions.configure<KoverReportExtension>(action)
