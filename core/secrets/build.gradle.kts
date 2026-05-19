import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.superdiary.multiplatform.kotlin")
    id("com.superdiary.android.library")
    id("com.superdiary.secrets")
}

tasks.named("generateBuildKonfig").dependsOn("prepareAndroidMainArtProfile")
