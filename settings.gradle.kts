import de.fayard.refreshVersions.bootstrapRefreshVersions

include(":diarycalendar")
buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}
bootstrapRefreshVersions()

rootProject.name = "superdiary"
include(":app")
include("materialcalendarview")