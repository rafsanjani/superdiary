package com.superdiary.gradle.secrets

import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import java.util.Properties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

data class Secret(
    val name: String,
    val value: String,
    val type: FieldSpec.Type,
)

/**
 * These secrets are only consumed in ":core:utils" module which is in turn
 * used by all other modules in the project
 */
class SecretsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.codingfeline.buildkonfig")

            buildKonfig {
                packageName = "com.foreverrafs.superdiary.secrets"

                defaultConfigs {
                    loadSecrets().forEach { secret ->
                        buildConfigField(
                            type = secret.type,
                            name = secret.name,
                            value = secret.value,
                        )
                    }
                }
            }
        }
    }

    private fun Project.loadPropertiesOrNull(): Properties? = try {
        Properties().apply {
            load(
                rootProject.file("secrets.properties").inputStream(),
            )
        }
    } catch (_: Throwable) {
        logger.warn(
            "secrets.properties not found. All app features will not work as expected",
        )
        null
    }

    private fun Project.loadSecrets(): List<Secret> {
        val properties = loadPropertiesOrNull() ?: return emptyList()

        fun getProperty(name: String): String = properties[name]?.toString() ?: run {
            logger.error("$name not provided!")
            ""
        }

        return listOf(
            Secret(
                name = "OPENAI_KEY",
                value = getProperty("OPENAI_KEY"),
                type = STRING,
            ),
            Secret(
                name = "GOOGLE_SERVER_CLIENT_ID",
                value = getProperty("GOOGLE_SERVER_CLIENT_ID"),
                type = STRING,
            ),
            Secret(
                name = "SUPABASE_URL",
                value = getProperty("SUPABASE_URL"),
                type = STRING,
            ),
            Secret(
                name = "SUPABASE_KEY",
                value = getProperty("SUPABASE_KEY"),
                type = STRING,
            ),
        )
    }
}

private fun Project.buildKonfig(action: BuildKonfigExtension.() -> Unit) =
    extensions.configure<BuildKonfigExtension>(action)
