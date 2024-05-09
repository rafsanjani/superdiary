package com.superdiary.gradle.codequality

object KoverExclusionList {
    val packages = listOf(
        "com.foreverrafs.superdiary.database",
        "db",
        "*.components",
        "*.di",
        "*.style",
        "*.generated.resources",
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
        "**/AppKt",
        "**/AppKt*",
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
        "**/androidApp/**",
        "**/DiaryListActions",
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
