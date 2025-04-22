import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.foreverrafs.superdiary.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    compileOnly(libs.conventionplugins.kover)
    compileOnly(libs.conventionplugins.ktlint)
    compileOnly(libs.conventionplugins.sonar)
    compileOnly(libs.conventionplugins.android)
    compileOnly(libs.conventionplugins.compose)
    compileOnly(libs.conventionplugins.compose.compiler)
    compileOnly(libs.conventionplugins.buildKonfig.compiler)
    compileOnly(libs.conventionplugins.buildKonfig.plugin)
}

gradlePlugin {
    val rootPackageName = "com.superdiary.gradle"

    plugins {
        register("GitHooksConventionPlugin") {
            id = "com.superdiary.githooks"
            implementationClass = "$rootPackageName.codequality.GitHooksConventionPlugin"
        }

        register("KotlinAndroidConventionPlugin") {
            id = "com.superdiary.kotlin.android"
            implementationClass = "$rootPackageName.kotlin.KotlinAndroidConventionPlugin"
        }

        register("KtlintConventionPlugin") {
            id = "com.superdiary.ktlint"
            implementationClass = "$rootPackageName.codequality.KtlintConventionPlugin"
        }

        register("SecretsConventionPlugin") {
            id = "com.superdiary.secrets"
            implementationClass = "$rootPackageName.secrets.SecretsConventionPlugin"
        }

        register("SnapshotsDiffPlugin") {
            id = "com.superdiary.snapshotdiff"
            implementationClass = "$rootPackageName.snapshots.SnapshotsDiffPlugin"
        }

        register("AndroidLibraryConventionPlugin") {
            id = "com.superdiary.android.library"
            implementationClass = "$rootPackageName.android.AndroidLibraryConventionPlugin"
        }

        register("AndroidComposeLibraryConventionPlugin") {
            id = "com.superdiary.compose.library"
            implementationClass = "$rootPackageName.android.AndroidComposeLibraryConventionPlugin"
        }

        register("KotlinMultiplatformConventionPlugin") {
            id = "com.superdiary.multiplatform.kotlin"
            implementationClass = "$rootPackageName.multiplatform.KotlinMultiplatformConventionPlugin"
        }

        register("ComposeMultiplatformConventionPlugin") {
            id = "com.superdiary.multiplatform.compose"
            implementationClass = "$rootPackageName.multiplatform.ComposeMultiplatformConventionPlugin"
        }
    }
}
