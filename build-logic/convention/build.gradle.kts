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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    compileOnly(libs.conventionplugins.kover)
    compileOnly(libs.conventionplugins.ktlint)
    compileOnly(libs.conventionplugins.sonar)
}

gradlePlugin {
    val rootPackageName = "com.superdiary.gradle"

    plugins {

        register("gitHooksConventionPlugin") {
            id = "com.superdiary.githooks"
            implementationClass = "$rootPackageName.codequality.GitHooksConventionPlugin"
        }

        register("kotlinAndroidPlugin") {
            id = "com.superdiary.kotlin.android"
            implementationClass = "$rootPackageName.kotlinjava.KotlinAndroidConventionPlugin"
        }

        register("koverConventionPlugin") {
            id = "com.superdiary.kover"
            implementationClass = "$rootPackageName.codequality.KoverConventionPlugin"
        }

        register("ktlintConventionPlugin") {
            id = "com.superdiary.ktlint"
            implementationClass = "$rootPackageName.codequality.KtlintConventionPlugin"
        }

        register("SonarConventionPlugin") {
            id = "com.superdiary.sonar"
            implementationClass = "$rootPackageName.codequality.SonarConventionPlugin"
        }
    }
}
