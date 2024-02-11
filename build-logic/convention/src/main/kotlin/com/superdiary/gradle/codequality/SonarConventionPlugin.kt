package com.superdiary.gradle.codequality

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.sonarqube.gradle.SonarExtension

class SonarConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.sonarqube")

            sonar {
                val exclusionList = KoverExclusionList.classes + KoverExclusionList.packages + KoverExclusionList.files

                properties {
                    property("sonar.projectKey", "rafsanjani_superdiary")
                    property("sonar.organization", "rafsanjani")
                    property("sonar.host.url", "https://sonarcloud.io")
                    property("sonar.coverage.exclusions", exclusionList)
                }
            }

            subprojects {
                sonar {
                    val buildDir = project.layout.buildDirectory.asFile.get()
                    val reportPath = "$buildDir/reports/kover/reportDebug.xml"
                    val lintReportPath = "$buildDir/reports/lint-results-debug.xml"

                    properties {
                        property("sonar.projectKey", "rafsanjani_superdiary")
                        property("sonar.organization", "rafsanjani")
                        property("sonar.host.url", "https://sonarcloud.io")
                        property("sonar.androidLint.reportPaths", lintReportPath)
                        property("sonar.coverage.jacoco.xmlReportPaths", reportPath)
                    }
                }
            }
        }
    }
}

private fun Project.sonar(block: SonarExtension.() -> Unit) = extensions.configure<SonarExtension>(block)
