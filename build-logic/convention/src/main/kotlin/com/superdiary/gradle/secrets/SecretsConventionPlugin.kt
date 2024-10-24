package com.superdiary.gradle.secrets

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import java.util.Properties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class SecretsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.codingfeline.buildkonfig")

            buildKonfig {
                // target.path returns something like ":shared-data" so strip the leading colon
                packageName = "com.foreverrafs.superdiary.${path.substring(1)}"

                val props = Properties()

                try {
                    props.load(
                        rootProject.file("secrets.properties").inputStream(),
                    )
                } catch (_: Exception) {
                    logger.warn(
                        "secrets.properties not found. All app features will not work as expected",
                    )
                }

                val openAIUrl: String = props["OPENAI_KEY"]?.toString() ?: run {
                    logger.error("OPENAI_KEY not provided!")
                    ""
                }

                defaultConfigs {
                    buildConfigField(
                        STRING,
                        "OPENAI_URL",
                        openAIUrl,
                    )
                }
            }
        }
    }
}

private fun Project.buildKonfig(action: BuildKonfigExtension.() -> Unit) =
    extensions.configure<BuildKonfigExtension>(action)
