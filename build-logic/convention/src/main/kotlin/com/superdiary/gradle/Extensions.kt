package com.superdiary.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

private val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.findVersion(alias: String): String = libs.findVersion(alias)
    .map { it.requiredVersion }
    .orElseThrow { IllegalArgumentException("Version not found") }
