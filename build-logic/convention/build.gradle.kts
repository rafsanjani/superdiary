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
    compileOnly("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.4")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
}

gradlePlugin {
    plugins {
        register("detektPlugin") {
            id = "com.superdiary.detekt"
            implementationClass = "codequality.DetektPlugin"
        }

        register("gitHooksPlugin") {
            id = "com.superdiary.githooks"
            implementationClass = "codequality.GitHooksPlugin"
        }
    }
}
