import de.fayard.refreshVersions.bootstrapRefreshVersions


buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.30.1")
}
bootstrapRefreshVersions()

rootProject.name = "superdiary"
include(":app")
include(":diarycalendar")