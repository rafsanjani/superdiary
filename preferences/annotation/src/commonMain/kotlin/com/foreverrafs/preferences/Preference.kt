package com.foreverrafs.preferences

@Suppress("unused")
@Target(AnnotationTarget.CLASS)
annotation class Preference(
    val generatedClassName: String,
)
