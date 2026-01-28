@file:Suppress("UnusedPrivateProperty")

plugins {
    id("com.superdiary.multiplatform.compose")
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.lifecycle.runtime.compose)
                implementation(libs.androidx.core.uri)

                // project dependency
                implementation(project(":core:sync"))
                implementation(project(":core:database"))
                implementation(project(":design-system"))

                implementation(libs.koin.compose.viewmodel)

                // TODO: Remove this dependency
                implementation(compose.materialIconsExtended)

                // feature modules
                implementation(project(":feature:diary-favorite"))
                implementation(project(":feature:diary-chat"))
                implementation(project(":feature:create-diary"))
                implementation(project(":feature:diary-profile"))
                implementation(project(":feature:diary-auth"))
                implementation(project(":feature:diary-dashboard"))
                implementation(project(":feature:diary-list"))

                implementation(project(":core:authentication"))
                implementation(project(":core:logging"))
                implementation(project(":core:diary-ai"))
                implementation(project(":core:analytics"))
                implementation(project(":core:permission"))
                implementation(project(":common-utils"))

                implementation(project(":shared-data"))
                implementation(libs.coil3.compose.core)
                implementation(libs.coil3.compose)
                implementation(libs.coil3.multiplatform)
                implementation(libs.jetbrains.compose.navigation3)
            }
        }

        commonTest {
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }

        androidUnitTest {
            dependencies {}
        }

        jvmMain {
            dependencies {
                implementation(libs.koin.jvm)
            }
        }
    }
}
