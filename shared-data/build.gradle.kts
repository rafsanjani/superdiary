@file:Suppress("UnusedPrivateProperty")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask


plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.mokkery)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.touchlab.stately)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.uuid)
                implementation(project(":preferences:annotation"))
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.supabase.posgrest)
                implementation(libs.supabase.realtime)

                // Project dependencies
                implementation(project(":common-utils"))
                implementation(project(":core:analytics"))
                implementation(project(":core:secrets"))
                implementation(project(":core:logging"))
                implementation(project(":core:location"))
                implementation(project(":core:database"))
            }

            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(project(":core:database-test"))
                implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
                implementation(project(":common-test"))
                implementation(libs.assertk.common)
            }
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }

        iosMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.darwin)
                implementation(libs.ktor.client.ios)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":preferences:processor"))
}

afterEvaluate {
    tasks.withType<KotlinCompilationTask<*>> {
        if (name != "kspCommonMainKotlinMetadata") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}
