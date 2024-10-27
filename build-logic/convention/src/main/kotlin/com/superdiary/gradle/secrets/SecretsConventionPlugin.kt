package com.superdiary.gradle.secrets

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import java.util.Properties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

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

                val props = Properties()

                try {
                    props.load(
                        rootProject.file("secrets.properties").inputStream(),
                    )
                } catch (_: Throwable) {
                    logger.warn(
                        "secrets.properties not found. All app features will not work as expected",
                    )
                }

                val openAIUrl: String = props["OPENAI_KEY"]?.toString() ?: run {
                    logger.error("OPENAI_KEY not provided!")
                    ""
                }

                val googleServerClientId: String =
                    props["GOOGLE_SERVER_CLIENT_ID"]?.toString() ?: run {
                        logger.error("GOOGLE_SERVER_CLIENT_ID not provided!")
                        ""
                    }

                defaultConfigs {
                    buildConfigField(
                        STRING,
                        "OPENAI_URL",
                        openAIUrl,
                    )
                }

                defaultConfigs {
                    buildConfigField(
                        STRING,
                        "GOOGLE_SERVER_CLIENT_ID",
                        googleServerClientId,
                    )
                }
            }
        }
    }
}

private fun Project.buildKonfig(action: BuildKonfigExtension.() -> Unit) =
    extensions.configure<BuildKonfigExtension>(action)
