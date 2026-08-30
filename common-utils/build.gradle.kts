plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.kotlin.datetime)
                kotlin("test")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.assertk.common)
                implementation(kotlin("test"))
            }
        }
    }
}
