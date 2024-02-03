package com.superdiary.gradle.codequality

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlinx.kover")

            kover {
                useJacoco("0.8.10")
            }

            koverReport {
                filters {
                    excludes {
                        packages(
                            "com.foreverrafs.superdiary.database",
                            "db",
                            "*.components",
                            "*.di",
                            "*.style",
                            "superdiary.shared-ui.generated.resources"
                        )
                        classes(
                            "*.*.*ScreenContent*",
                            "*.*.*Preview*",
                            "*.*.*AppKt*",
                            "*.*.*CreateDiaryScreen",
                            "*.*.Resources*",
                            "*.*.Main*",
                            "*.*.*Screen*",
                            "*.*.*Tab",
                            "*.*.*ComposableSingletons*",
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

private fun Project.kover(
    action: KoverProjectExtension.() -> Unit
) = extensions.configure<KoverProjectExtension>(action)