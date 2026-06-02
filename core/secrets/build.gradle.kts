import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(conventionLibs.plugins.superdiary.multiplatform.kotlin)
    alias(conventionLibs.plugins.superdiary.android.library)
    alias(conventionLibs.plugins.superdiary.secrets)
}

tasks.named("generateBuildKonfig").dependsOn("prepareAndroidMainArtProfile")
