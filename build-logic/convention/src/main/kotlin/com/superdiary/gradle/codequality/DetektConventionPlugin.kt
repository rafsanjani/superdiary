package com.superdiary.gradle.codequality

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            configureDetekt()
        }
    }
}

private fun Project.configureDetekt() {
    println("Rafsanjani $name")
    val configFile = files("$rootDir/quality/detekt-rules.yml")
    val sourceFiles = file(rootDir)
    val reportFile = file("${layout.buildDirectory.asFile.get()}/reports/detekt")
    val kotlinFiles = "**/*.kt"
    val resourceFiles = "**/resources/**"
    val buildFiles = "**/build/**"

    tasks.register("detektAll", Detekt::class.java) {
        config.setFrom(configFile)
        buildUponDefaultConfig = true
        reportsDir.set(reportFile)
        ignoreFailures = project.hasProperty("ignoreFailures")
        setSource(sourceFiles)
        include(kotlinFiles)
        exclude(resourceFiles, buildFiles)

        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(false)
        }
    }
}
