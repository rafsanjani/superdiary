plugins {
    id("com.superdiary.multiplatform.kotlin")
}

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(projects.preferences.annotation)
            implementation(libs.square.kotlinPoet)
            implementation("com.squareup:kotlinpoet-ksp:2.2.0")
            implementation("com.google.devtools.ksp:symbol-processing-api:2.3.3")
        }
    }
}
