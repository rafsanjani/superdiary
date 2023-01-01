plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight").version("1.5.4")
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlin.coroutines.native)
                implementation(libs.kotlin.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.square.sqldelight.driver.android)
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.4")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.2.1")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("com.squareup.sqldelight:native-driver:1.5.4")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

sqldelight {
    database("KmpSuperDiaryDB") {
        packageName = "db"
        sourceFolders = listOf("sqldelight")
    }
}

android {
    namespace = "com.foreverrafs.superdiary"
    compileSdk = 33
    defaultConfig {
        minSdk = 28
        targetSdk = 33
    }
}
