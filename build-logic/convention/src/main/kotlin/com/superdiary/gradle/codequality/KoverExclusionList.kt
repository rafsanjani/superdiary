package com.superdiary.gradle.codequality

object KoverExclusionList {
    val packages = listOf(
        "com.foreverrafs.superdiary.database",
        "db",
        "*.components",
        "*.di",
        "*.style",
        "superdiary.shared-ui.generated.resources",
    )

    val classes = listOf(
        "**/*Preview*",
        // Compose screens
        "**/*Screen*",
        "**/*Tab*",
        // Koin modules and all that shit
        "**/di/*",
        // iOS view controller
        "**/ui/ViewController*",
        // The entry-point UI of the app
        "**/App.kt",
        // The android application class
        "**/DiaryApp.kt",
        // JVM App entrypoint
        "**/Main.kt",
        // SqlDelight database driver
        "**/*DatabaseDriver*",
        // Sqlite Database file
        "**/*Database",
        // Reusable screen components
        "**/components/**",
        "**/*Resources*",
        "*.*.*ComposableSingletons*",
        "**/app/**",
        "**/androidApp/**"
    )

    val files = listOf(
        "BottomNavigationScreenKt",
        "desktopApp.*",
        "desktopApp/**",
        "androidApp/**",
        "swipe/src/commonMain/kotlin/me/saket/swipe/SwipeableActionBox.kt",
        "swipe/src/commonMain/kotlin/me/saket/swipe/SwipeAction.kt",
        "**/Module.kt",
        "**/defaults.kt",
        "**/*.ios.kt",
        "**/*.jvm.kt",
        "**/*.android.kt",
    )
}
