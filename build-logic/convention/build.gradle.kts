import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
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
    compileOnly("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.6")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    compileOnly("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.6")
    compileOnly("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:12.1.0")
    compileOnly("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:4.4.1.3373")
}

gradlePlugin {
    val rootPackageName = "com.superdiary.gradle"

    plugins {
        register("detektConventionPlugin") {
            id = "com.superdiary.detekt"
            implementationClass = "$rootPackageName.codequality.DetektConventionPlugin"
        }

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
